package uk.co.mruoc.cws.solver.chatgpt;

import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionContentPart;
import com.openai.models.chat.completions.ChatCompletionContentPartText;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.chat.completions.ChatCompletionMessage;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.mruoc.cws.entity.Clues;
import uk.co.mruoc.cws.entity.Id;
import uk.co.mruoc.cws.solver.SortCluesPromptTextFactory;
import uk.co.mruoc.cws.usecase.ClueSorter;

@RequiredArgsConstructor
@Slf4j
public class ChatGptClueSorter implements ClueSorter {

  private final OpenAIClient client;
  private final ChatModel chatModel;
  private final SortCluesPromptTextFactory promptTextFactory;

  public ChatGptClueSorter(OpenAIClient client) {
    this(client, ChatModel.GPT_4_1_NANO);
  }

  public ChatGptClueSorter(OpenAIClient client, ChatModel chatModel) {
    this(client, chatModel, new SortCluesPromptTextFactory());
  }

  @Override
  public Clues sort(Clues clues) {
    var request = toSortCluesRequest(clues);
    var response = client.chat().completions().create(request);
    var sortedIds = toSortedIds(response);
    return clues.sortByIds(sortedIds);
  }

  private ChatCompletionCreateParams toSortCluesRequest(Clues clues) {
    var prompt =
        ChatCompletionContentPartText.builder().text(promptTextFactory.toPromptText(clues)).build();
    var parts = List.of(ChatCompletionContentPart.ofText(prompt));
    return ChatCompletionCreateParams.builder()
        .model(chatModel)
        .addUserMessageOfArrayOfContentParts(parts)
        .build();
  }

  private Collection<Id> toSortedIds(ChatCompletion response) {
    var string = toString(response);
    return toSortedIds(string);
  }

  private String toString(ChatCompletion response) {
    return response.choices().stream()
        .map(ChatCompletion.Choice::message)
        .map(ChatCompletionMessage::content)
        .flatMap(Optional::stream)
        .collect(Collectors.joining());
  }

  private Collection<Id> toSortedIds(String input) {
    return Arrays.stream(input.split(System.lineSeparator()))
        .map(line -> line.split(":")[0].trim())
        .map(Id::new)
        .toList();
  }
}
