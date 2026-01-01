package uk.co.mruoc.cws.app.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import software.amazon.awssdk.regions.Region;
import uk.co.mruoc.cws.solver.bedrock.BedrockClientConfig;
import uk.co.mruoc.cws.solver.bedrock.BedrockConversationConfig;

@ConfigurationProperties(prefix = "bedrock")
public record BedrockSolverClientConfigProperties(Client client, Conversation conversation) {

  public record Client(
      Region region,
      Duration connectionTimeout,
      Duration socketTimeout,
      Duration apiCallAttemptTimeout,
      Duration apiCallTimeout)
      implements BedrockClientConfig {
    // intentionally blank
  }

  public record Conversation(String modelId, float temperature, int maxTokens)
      implements BedrockConversationConfig {
    // intentionally blank
  }
}
