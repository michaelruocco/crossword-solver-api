package uk.co.mruoc.cws.solver.bedrock;

public interface BedrockConversationConfig {

  String modelId();

  float temperature();

  int maxTokens();
}
