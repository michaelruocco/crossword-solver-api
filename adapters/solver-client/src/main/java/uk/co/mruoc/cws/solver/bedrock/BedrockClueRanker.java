package uk.co.mruoc.cws.solver.bedrock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.ContentBlock;
import software.amazon.awssdk.services.bedrockruntime.model.ConversationRole;
import software.amazon.awssdk.services.bedrockruntime.model.ConverseRequest;
import software.amazon.awssdk.services.bedrockruntime.model.Message;
import uk.co.mruoc.cws.entity.Clues;
import uk.co.mruoc.cws.solver.ClueListConverter;
import uk.co.mruoc.cws.solver.ClueRankerPromptTextFactory;
import uk.co.mruoc.cws.usecase.ClueRanker;

@RequiredArgsConstructor
@Slf4j
public class BedrockClueRanker implements ClueRanker {

  private final BedrockRuntimeClient client;
  private final String modelId;
  private final ClueRankerPromptTextFactory promptTextFactory;
  private final ClueListConverter clueListConverter;

  public BedrockClueRanker(BedrockRuntimeClient client) {
    this(client, ModelId.DEFAULT);
  }

  public BedrockClueRanker(BedrockRuntimeClient client, String modelId) {
    this(client, modelId, new ClueRankerPromptTextFactory(), new ClueListConverter());
  }

  @Override
  public Clues rankByEase(Clues clues) {
    var promptText = promptTextFactory.toPromptText(clues);
    var rankedClueList = execute(promptText);
    log.debug("ranked clue list {}", rankedClueList);
    var rankedIds = clueListConverter.toIds(rankedClueList);
    return clues.sortByIds(rankedIds);
  }

  private String execute(String promptText) {
    var message =
        Message.builder()
            .content(ContentBlock.fromText(promptText))
            .role(ConversationRole.USER)
            .build();
    var request =
        ConverseRequest.builder()
            .modelId(modelId)
            .messages(message)
            // TODO make the inference config injectable and created in one place
            // if it will be the same for this and answer finder
            .inferenceConfig(config -> config.temperature(0.2f).maxTokens(2024))
            .build();
    var response = client.converse(request);
    return response.output().message().content().getFirst().text();
  }
}
