package uk.co.mruoc.cws.solver.chatgpt;

import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionContentPart;
import com.openai.models.chat.completions.ChatCompletionContentPartImage;
import com.openai.models.chat.completions.ChatCompletionContentPartText;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.chat.completions.ChatCompletionMessage;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.mruoc.cws.entity.Clues;
import uk.co.mruoc.cws.solver.ClueExtractorResponseConverter;
import uk.co.mruoc.cws.usecase.ClueExtractor;
import uk.co.mruoc.cws.usecase.Image;
import uk.co.mruoc.file.FileLoader;

@RequiredArgsConstructor
@Slf4j
public class ChatGptClueExtractor implements ClueExtractor {

  private final OpenAIClient client;
  private final ChatModel chatModel;
  private final String promptText;
  private final ClueExtractorResponseConverter responseConverter;

  public ChatGptClueExtractor(OpenAIClient client) {
    this(client, ChatModel.GPT_4_1_NANO);
  }

  public ChatGptClueExtractor(OpenAIClient client, ChatModel chatModel) {
    this(
        client,
        chatModel,
        FileLoader.loadContentFromClasspath("prompts/extract-clues.txt"),
        new ClueExtractorResponseConverter());
  }

  @Override
  public Clues extractClues(Image image) {
    var request = toExtractCluesRequest(image);
    var response = client.chat().completions().create(request);
    return responseConverter.toClues(toString(response));
  }

  private ChatCompletionCreateParams toExtractCluesRequest(Image image) {
    log.info("extracting clues from image {} using prompt {}", image.getName(), promptText);
    var prompt = ChatCompletionContentPartText.builder().text(promptText).build();
    var base64 = Base64.getEncoder().encodeToString(image.getBytes());
    var imageUrl = String.format("data:image/png;base64,%s", base64);
    var imagePart =
        ChatCompletionContentPartImage.builder()
            .imageUrl(ChatCompletionContentPartImage.ImageUrl.builder().url(imageUrl).build())
            .build();
    var parts =
        List.of(
            ChatCompletionContentPart.ofText(prompt),
            ChatCompletionContentPart.ofImageUrl(imagePart));
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
}
