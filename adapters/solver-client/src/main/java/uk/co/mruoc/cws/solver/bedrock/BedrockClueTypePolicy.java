package uk.co.mruoc.cws.solver.bedrock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import uk.co.mruoc.cws.entity.ClueType;
import uk.co.mruoc.cws.entity.Clues;
import uk.co.mruoc.cws.solver.DeterminePuzzleTypePromptTextFactory;
import uk.co.mruoc.cws.usecase.ClueTypePolicy;

@Slf4j
@RequiredArgsConstructor
public class BedrockClueTypePolicy implements ClueTypePolicy {

  private final PromptTextExecutor promptTextExecutor;
  private final DeterminePuzzleTypePromptTextFactory promptTextFactory;

  public BedrockClueTypePolicy(BedrockRuntimeClient client) {
    this(client, new DefaultBedrockConversationConfig());
  }

  public BedrockClueTypePolicy(
      BedrockRuntimeClient client, BedrockConversationConfig conversationConfig) {
    this(new PromptTextExecutor(client, conversationConfig));
  }

  public BedrockClueTypePolicy(PromptTextExecutor promptTextExecutor) {
    this(promptTextExecutor, new DeterminePuzzleTypePromptTextFactory());
  }

  @Override
  public ClueType determineClueType(Clues clues) {
    var promptText = promptTextFactory.toPromptText(clues);
    var result = promptTextExecutor.execute(promptText);
    log.debug("determined clue type {}", result);
    return ClueType.valueOf(result);
  }
}
