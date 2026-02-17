package uk.co.mruoc.cws.app.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import uk.co.mruoc.cws.solver.bedrock.BedrockAnswerFinder;
import uk.co.mruoc.cws.solver.bedrock.BedrockClueExtractor;
import uk.co.mruoc.cws.solver.bedrock.BedrockClueRanker;
import uk.co.mruoc.cws.solver.bedrock.BedrockClueTypePolicy;
import uk.co.mruoc.cws.solver.bedrock.PromptTextExecutor;
import uk.co.mruoc.cws.usecase.AnswerFinder;
import uk.co.mruoc.cws.usecase.ClueExtractor;
import uk.co.mruoc.cws.usecase.ClueRanker;
import uk.co.mruoc.cws.usecase.ClueTypePolicy;

@RequiredArgsConstructor
@Configuration
@EnableConfigurationProperties(BedrockSolverClientConfigProperties.class)
public class BedrockSolverClientConfig {

  private final BedrockSolverClientConfigProperties properties;

  @Bean
  public BedrockRuntimeClient bedrockRuntimeClient() {
    var clientProperties = properties.client();
    return BedrockRuntimeClient.builder()
        .credentialsProvider(DefaultCredentialsProvider.builder().build())
        .region(clientProperties.region())
        .httpClientBuilder(
            UrlConnectionHttpClient.builder()
                .connectionTimeout(clientProperties.connectionTimeout())
                .socketTimeout(clientProperties.socketTimeout()))
        .overrideConfiguration(
            ClientOverrideConfiguration.builder()
                .apiCallAttemptTimeout(clientProperties.apiCallAttemptTimeout())
                .apiCallTimeout(clientProperties.apiCallTimeout())
                .build())
        .build();
  }

  @Bean
  public PromptTextExecutor promptTextExecutor(BedrockRuntimeClient client) {
    var conversationProperties = properties.conversation();
    return new PromptTextExecutor(client, conversationProperties);
  }

  @Bean
  public ClueExtractor bedrockClueExtractor(BedrockRuntimeClient client) {
    var conversationProperties = properties.conversation();
    return new BedrockClueExtractor(client, conversationProperties.modelId());
  }

  @Bean
  public AnswerFinder bedrockAnswerFinder(PromptTextExecutor promptTextExecutor) {
    return new BedrockAnswerFinder(promptTextExecutor);
    // return new CachingAnswerFinder();
  }

  @Bean
  public ClueRanker bedrockClueRanker(PromptTextExecutor promptTextExecutor) {
    return new BedrockClueRanker(promptTextExecutor);
  }

  @Bean
  public ClueTypePolicy bedrockClueTypePolicy(PromptTextExecutor promptTextExecutor) {
    return new BedrockClueTypePolicy(promptTextExecutor);
  }
}
