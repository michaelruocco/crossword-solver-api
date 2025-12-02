package uk.co.mruoc.cws.solver.bedrock;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BedrockRuntimeClientFactory {

  public static BedrockRuntimeClient buildClient() {
    return BedrockRuntimeClient.builder()
        .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
        .region(Region.EU_WEST_1)
        .build();
  }
}
