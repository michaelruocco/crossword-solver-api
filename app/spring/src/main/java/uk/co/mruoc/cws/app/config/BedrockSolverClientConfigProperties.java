package uk.co.mruoc.cws.app.config;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import software.amazon.awssdk.regions.Region;
import uk.co.mruoc.cws.solver.bedrock.BedrockClientConfig;
import uk.co.mruoc.cws.solver.bedrock.BedrockConversationConfig;
import uk.co.mruoc.cws.solver.bedrock.BedrockModelConfig;

@ConfigurationProperties(prefix = "bedrock")
public record BedrockSolverClientConfigProperties(
    Client client, Conversation conversation, Model model) implements BedrockModelConfig {

  @Override
  public String answerFinderId() {
    return model.answerFinderId;
  }

  @Override
  public String clueExtractorId() {
    return model.clueExtractorId;
  }

  @Override
  public String clueRankerId() {
    return model.clueRankerId;
  }

  @Override
  public String clueTypePolicyId() {
    return model.clueTypePolicyId;
  }

  public record Client(
      Region region,
      Duration connectionTimeout,
      Duration socketTimeout,
      Duration apiCallAttemptTimeout,
      Duration apiCallTimeout)
      implements BedrockClientConfig {
    // intentionally blank
  }

  public record Conversation(float temperature, int maxTokens)
      implements BedrockConversationConfig {
    // intentionally blank
  }

  public record Model(
      String answerFinderId, String clueExtractorId, String clueRankerId, String clueTypePolicyId)
      implements BedrockModelConfig {
    // intentionally blank
  }
}
