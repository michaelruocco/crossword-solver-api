package uk.co.mruoc.cws.solver.bedrock;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import uk.co.mruoc.cws.usecase.AnswerFinder;
import uk.co.mruoc.cws.usecase.ClueExtractor;
import uk.co.mruoc.cws.usecase.WordExtractor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BedrockRuntimeClientFactory {

  public static final String DEFAULT_MODEL_ID = "eu.anthropic.claude-3-7-sonnet-20250219-v1:0";

  public static ClueExtractor buildClueExtractor() {
    return new BedrockClueExtractor(buildClient(), DEFAULT_MODEL_ID);
  }

  public static WordExtractor buildWordExtractor() {
    return new BedrockWordExtractor(buildClient(), DEFAULT_MODEL_ID);
  }

  public static AnswerFinder buildAnswerFinder() {
    return new BedrockAnswerFinder(buildClient(), DEFAULT_MODEL_ID);
  }

  private static BedrockRuntimeClient buildClient() {
    return BedrockRuntimeClient.builder()
        .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
        .region(Region.EU_WEST_1)
        .build();
  }
}
