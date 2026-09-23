package uk.co.mruoc.cws.solver.bedrock;

public class DefaultBedrockConversationConfig implements BedrockConversationConfig {
  @Override
  public String modelId() {
    return "eu.anthropic.claude-opus-4-6-v1";
  }

  @Override
  public float temperature() {
    return 0.2f;
  }

  @Override
  public int maxTokens() {
    return 2024;
  }
}
