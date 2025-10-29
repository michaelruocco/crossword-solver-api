package uk.co.mruoc.cws.solver.chatgpt;

import com.openai.client.OpenAIClient;
import com.openai.errors.RateLimitException;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionContentPart;
import com.openai.models.chat.completions.ChatCompletionContentPartText;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.chat.completions.ChatCompletionMessage;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.mruoc.cws.entity.Answer;
import uk.co.mruoc.cws.entity.Answers;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.entity.Clues;
import uk.co.mruoc.cws.solver.FindAnswerPromptTextFactory;
import uk.co.mruoc.cws.solver.FindAnswerResponseConverter;
import uk.co.mruoc.cws.usecase.AnswerFinder;

@RequiredArgsConstructor
@Slf4j
public class ChatGptAnswerFinder implements AnswerFinder {

  private final OpenAIClient client;
  private final ChatModel chatModel;
  private final FindAnswerPromptTextFactory promptTextFactory;
  private final FindAnswerResponseConverter responseConverter;
  private final Retry retry;

  public ChatGptAnswerFinder(OpenAIClient client, ChatModel chatModel) {
    this(
        client,
        chatModel,
        new FindAnswerPromptTextFactory(),
        new FindAnswerResponseConverter(),
        buildDefaultRetry());
  }

  @Override
  public Answers findAnswers(Clues clues) {
    return Retry.decorateFunction(retry, this::doFindAnswers).apply(clues);
  }

  @Override
  public Answer findAnswer(Clue clue) {
    return Retry.decorateFunction(retry, this::doFindAnswer).apply(clue);
  }

  public Answers doFindAnswers(Clues clues) {
    var request = toBatchFindAnswerRequest(clues);
    var response = client.chat().completions().create(request);
    return responseConverter.toAnswers(toString(response));
  }

  private Answer doFindAnswer(Clue clue) {
    var request = toFindAnswerRequest(clue);
    var response = client.chat().completions().create(request);
    return responseConverter.toAnswer(toString(response));
  }

  private ChatCompletionCreateParams toFindAnswerRequest(Clue clue) {
    var promptText = promptTextFactory.toPromptText(clue);
    var prompt = ChatCompletionContentPartText.builder().text(promptText).build();
    var parts = List.of(ChatCompletionContentPart.ofText(prompt));
    return ChatCompletionCreateParams.builder()
        .model(chatModel)
        .addUserMessageOfArrayOfContentParts(parts)
        .build();
  }

  private ChatCompletionCreateParams toBatchFindAnswerRequest(Clues clues) {
    var promptText = promptTextFactory.toPromptText(clues);
    var prompt = ChatCompletionContentPartText.builder().text(promptText).build();
    var parts = List.of(ChatCompletionContentPart.ofText(prompt));
    return ChatCompletionCreateParams.builder()
        .model(chatModel)
        .addUserMessageOfArrayOfContentParts(parts)
        .build();
  }

  private String toString(ChatCompletion response) {
    return response.choices().stream()
        .map(ChatCompletion.Choice::message)
        .map(ChatCompletionMessage::content)
        .flatMap(Optional::stream)
        .collect(Collectors.joining());
  }

  private static Retry buildDefaultRetry() {
    var config =
        RetryConfig.custom()
            .maxAttempts(5)
            .intervalFunction(IntervalFunction.ofExponentialBackoff(Duration.ofSeconds(20), 2))
            .retryExceptions(RateLimitException.class)
            .failAfterMaxAttempts(true)
            .build();
    var name = String.format("%s-retry", ChatGptAnswerFinder.class.getName());
    return Retry.of(name, config);
  }
}
