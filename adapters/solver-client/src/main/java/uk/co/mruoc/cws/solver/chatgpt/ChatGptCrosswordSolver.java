package uk.co.mruoc.cws.solver.chatgpt;

import static uk.co.mruoc.file.FileLoader.loadContentFromClasspath;

import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionContentPart;
import com.openai.models.chat.completions.ChatCompletionContentPartImage;
import com.openai.models.chat.completions.ChatCompletionContentPartText;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.chat.completions.ChatCompletionMessage;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.mruoc.cws.entity.Words;
import uk.co.mruoc.cws.solver.JsonMapper;
import uk.co.mruoc.cws.usecase.WordExtractor;

@RequiredArgsConstructor
@Slf4j
public class ChatGptCrosswordSolver implements WordExtractor {

  private final OpenAIClient client;
  private final ChatModel chatModel;
  private final String extractWordsPrompt;
  private final JsonMapper mapper;

  public ChatGptCrosswordSolver(OpenAIClient client) {
    this(client, ChatModel.GPT_4_1_NANO);
  }

  public ChatGptCrosswordSolver(OpenAIClient client, ChatModel chatModel) {
    this(
        client, chatModel, loadContentFromClasspath("prompts/extract-words.txt"), new JsonMapper());
  }

  @Override
  public Words extractWords(String imageUrl) {
    var request = toExtractWordsRequest(imageUrl);
    var response = client.chat().completions().create(request);
    return toWords(response);
  }

  private ChatCompletionCreateParams toExtractWordsRequest(String imageUrl) {
    log.info("extracting words from image {} using prompt {}", imageUrl, extractWordsPrompt);
    var prompt = ChatCompletionContentPartText.builder().text(extractWordsPrompt).build();
    var image =
        ChatCompletionContentPartImage.builder()
            .imageUrl(ChatCompletionContentPartImage.ImageUrl.builder().url(imageUrl).build())
            .build();
    var parts =
        List.of(
            ChatCompletionContentPart.ofText(prompt), ChatCompletionContentPart.ofImageUrl(image));
    return ChatCompletionCreateParams.builder()
        .model(chatModel)
        .addUserMessageOfArrayOfContentParts(parts)
        .build();
  }

  private Words toWords(ChatCompletion response) {
    var json = toString(response);
    log.info("got words {}", json);
    return mapper.toWords(json);
  }

  private String toString(ChatCompletion response) {
    return response.choices().stream()
        .map(ChatCompletion.Choice::message)
        .map(ChatCompletionMessage::content)
        .flatMap(Optional::stream)
        .collect(Collectors.joining());
  }
}
