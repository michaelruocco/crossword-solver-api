package uk.co.mruoc.cws.app.config;

import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import uk.co.mruoc.cws.solver.bedrock.BedrockAnswerFinder;
import uk.co.mruoc.cws.solver.bedrock.BedrockClueExtractor;
import uk.co.mruoc.cws.solver.bedrock.BedrockClueRanker;
import uk.co.mruoc.cws.usecase.AnswerFinder;
import uk.co.mruoc.cws.usecase.CachingAnswerFinder;
import uk.co.mruoc.cws.usecase.ClueExtractor;
import uk.co.mruoc.cws.usecase.ClueRanker;

@Configuration
public class BedrockSolverClientConfig {

  // TODO make configuration param
  private final String model = "eu.anthropic.claude-3-7-sonnet-20250219-v1:0";

  @Bean
  public BedrockRuntimeClient bedrockRuntimeClient() {
    return BedrockRuntimeClient.builder()
        .credentialsProvider(DefaultCredentialsProvider.builder().build())
        .region(Region.EU_WEST_1)
        .httpClientBuilder(
            UrlConnectionHttpClient.builder()
                .connectionTimeout(Duration.ofSeconds(30))
                .socketTimeout(Duration.ofSeconds(180)))
        .overrideConfiguration(
            ClientOverrideConfiguration.builder()
                .apiCallAttemptTimeout(Duration.ofMinutes(2))
                .apiCallTimeout(Duration.ofMinutes(4))
                .build())
        .build();
  }

  @Bean
  public AnswerFinder bedrockAnswerFinder(BedrockRuntimeClient client) {
    return new CachingAnswerFinder(new BedrockAnswerFinder(client, model));
  }

  @Bean
  public ClueExtractor bedrockClueExtractor(BedrockRuntimeClient client) {
    return new BedrockClueExtractor(client, model);
  }

  @Bean
  public ClueRanker bedrockClueRanker(BedrockRuntimeClient client) {
    return new BedrockClueRanker(client, model);
  }
}
