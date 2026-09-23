package uk.co.mruoc.cws.solver.bedrock;

public class DefaultBedrockModelConfig implements BedrockModelConfig {

  private static final String OPUS_ID = "eu.anthropic.claude-opus-4-6-v1";

  @Override
  public String answerFinderId() {
    return OPUS_ID;
  }

  @Override
  public String clueExtractorId() {
    return OPUS_ID;
  }

  @Override
  public String clueRankerId() {
    return OPUS_ID;
  }

  @Override
  public String clueTypePolicyId() {
    return "eu.anthropic.claude-sonnet-4-6";
  }
}
