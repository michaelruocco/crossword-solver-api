package uk.co.mruoc.cws.solver.bedrock;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.ContentBlock;
import software.amazon.awssdk.services.bedrockruntime.model.ConversationRole;
import software.amazon.awssdk.services.bedrockruntime.model.ConverseRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InferenceConfiguration;
import software.amazon.awssdk.services.bedrockruntime.model.Message;

@RequiredArgsConstructor
public class PromptTextExecutor {

  private final BedrockRuntimeClient client;
  private final BedrockConversationConfig conversationConfig;

  public String execute(String promptText) {
    var message = toUserMessage(promptText);
    var request = toConversationRequest(message);
    var response = client.converse(request);
    return response.output().message().content().getFirst().text();
  }

  private Message toUserMessage(String promptText) {
    return Message.builder()
        .content(ContentBlock.fromText(promptText))
        .role(ConversationRole.USER)
        .build();
  }

  private ConverseRequest toConversationRequest(Message message) {
    return ConverseRequest.builder()
        .modelId(conversationConfig.modelId())
        .messages(message)
        .inferenceConfig(buildInferenceConfig())
        .build();
  }

  private InferenceConfiguration buildInferenceConfig() {
    return InferenceConfiguration.builder()
        .temperature(conversationConfig.temperature())
        .maxTokens(conversationConfig.maxTokens())
        .build();
  }
}
