package uk.co.mruoc.cws.app.config;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.mruoc.cws.solver.chatgpt.ChatGptAnswerFinder;
import uk.co.mruoc.cws.solver.chatgpt.ChatGptClueExtractor;
import uk.co.mruoc.cws.usecase.AnswerFinder;
import uk.co.mruoc.cws.usecase.ClueExtractor;

@Configuration
@ConditionalOnProperty("open.ai.api.key")
public class ChatGptSolverClientConfig {

  @Value("open.ai.api.key")
  private String apiKey;

  @Value("${chat.gpt.model:gpt-4.1-nano}")
  private String chatGptModel;

  @Bean
  public OpenAIClient openAIClient() {
    return OpenAIOkHttpClient.builder().apiKey(apiKey).build();
  }

  @ConditionalOnMissingBean
  @Bean
  public AnswerFinder chatGptAnswerFinder(OpenAIClient client) {
    return new ChatGptAnswerFinder(client, ChatModel.of(chatGptModel));
  }

  @Bean
  public ClueExtractor chatGptClueExtractor(OpenAIClient client) {
    return new ChatGptClueExtractor(client, ChatModel.of(chatGptModel));
  }
}
