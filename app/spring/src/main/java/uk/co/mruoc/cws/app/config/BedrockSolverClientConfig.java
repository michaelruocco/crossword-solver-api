package uk.co.mruoc.cws.app.config;

import java.util.function.Function;
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
import uk.co.mruoc.cws.solver.bedrock.BedrockClueTypePolicy;
import uk.co.mruoc.cws.solver.bedrock.PromptTextExecutor;
import uk.co.mruoc.cws.usecase.AnswerFinder;
import uk.co.mruoc.cws.usecase.ClueExtractor;
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
  public ClueExtractor bedrockClueExtractor(BedrockRuntimeClient client) {
    return new BedrockClueExtractor(client, properties.clueExtractorId());
  }

  @Bean
  public AnswerFinder bedrockAnswerFinder(BedrockRuntimeClient client) {
    var promptTextExecutor =
        buildTextExecutor(client, BedrockSolverClientConfigProperties::answerFinderId);
    return new BedrockAnswerFinder(promptTextExecutor);
  }

  @Bean
  public ClueTypePolicy bedrockClueTypePolicy(BedrockRuntimeClient client) {
    var promptTextExecutor =
        buildTextExecutor(client, BedrockSolverClientConfigProperties::clueTypePolicyId);
    return new BedrockClueTypePolicy(promptTextExecutor);
  }

  private PromptTextExecutor buildTextExecutor(
      BedrockRuntimeClient client,
      Function<BedrockSolverClientConfigProperties, String> modelIdProvider) {
    return PromptTextExecutor.builder()
        .client(client)
        .conversationConfig(properties.conversation())
        .modelId(modelIdProvider.apply(properties))
        .build();
  }
}
