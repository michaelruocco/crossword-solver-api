package uk.co.mruoc.cws.solver.bedrock;

public class DefaultBedrockConversationConfig implements BedrockConversationConfig {
  @Override
  public String modelId() {
    return "eu.anthropic.claude-3-7-sonnet-20250219-v1:0";
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
