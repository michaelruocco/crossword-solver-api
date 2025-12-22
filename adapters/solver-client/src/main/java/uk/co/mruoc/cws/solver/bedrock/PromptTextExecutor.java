package uk.co.mruoc.cws.solver.bedrock;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.ContentBlock;
import software.amazon.awssdk.services.bedrockruntime.model.ConversationRole;
import software.amazon.awssdk.services.bedrockruntime.model.ConverseRequest;
import software.amazon.awssdk.services.bedrockruntime.model.Message;

@RequiredArgsConstructor
public class PromptTextExecutor {

  private final BedrockRuntimeClient client;
  private final String modelId;

  public PromptTextExecutor(BedrockRuntimeClient client) {
    this(client, ModelId.DEFAULT);
  }

  public String execute(String promptText) {
    var message =
        Message.builder()
            .content(ContentBlock.fromText(promptText))
            .role(ConversationRole.USER)
            .build();
    var request =
        ConverseRequest.builder()
            .modelId(modelId)
            .messages(message)
            .inferenceConfig(config -> config.temperature(0.0f).maxTokens(2024))
            .build();
    var response = client.converse(request);
    return response.output().message().content().getFirst().text();
  }
}
