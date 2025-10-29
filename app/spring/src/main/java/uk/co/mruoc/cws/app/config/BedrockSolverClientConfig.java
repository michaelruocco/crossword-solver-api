package uk.co.mruoc.cws.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import uk.co.mruoc.cws.solver.bedrock.BedrockAnswerFinder;
import uk.co.mruoc.cws.usecase.AnswerFinder;

@Configuration
public class BedrockSolverClientConfig {

  @Bean
  public BedrockRuntimeClient bedrockRuntimeClient() {
    return BedrockRuntimeClient.builder()
        .credentialsProvider(DefaultCredentialsProvider.builder().build())
        .region(Region.EU_WEST_1)
        .build();
  }

  @Bean
  public AnswerFinder bedrockAnswerFinder(BedrockRuntimeClient client) {
    var model = "eu.anthropic.claude-3-7-sonnet-20250219-v1:0";
    return new BedrockAnswerFinder(client, model);
  }
}
