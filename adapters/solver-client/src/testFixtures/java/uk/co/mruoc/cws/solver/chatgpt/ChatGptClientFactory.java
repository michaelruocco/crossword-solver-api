package uk.co.mruoc.cws.solver.chatgpt;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import uk.co.mruoc.cws.usecase.AnswerFinder;
import uk.co.mruoc.cws.usecase.ClueSorter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatGptClientFactory {

  public static final ChatModel DEFAULT_MODEL = ChatModel.GPT_3_5_TURBO;

  public static ClueSorter buildClueSorter() {
    return new ChatGptClueSorter(buildClient(), DEFAULT_MODEL);
  }

  public static AnswerFinder buildAnswerFinder() {
    return new ChatGptAnswerFinder(buildClient(), DEFAULT_MODEL);
  }

  private static OpenAIClient buildClient() {
    var apiKey = System.getenv("OPEN_AI_API_KEY");
    return OpenAIOkHttpClient.builder().apiKey(apiKey).build();
  }
}
