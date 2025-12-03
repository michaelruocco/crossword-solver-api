package uk.co.mruoc.cws.solver.chatgpt;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatGptClientFactory {

  public static OpenAIClient buildClient() {
    var apiKey = System.getenv("OPEN_AI_API_KEY");
    return OpenAIOkHttpClient.builder().apiKey(apiKey).build();
  }
}
