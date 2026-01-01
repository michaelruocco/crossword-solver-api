package uk.co.mruoc.cws.solver.bedrock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import uk.co.mruoc.cws.entity.Clues;
import uk.co.mruoc.cws.solver.ClueListConverter;
import uk.co.mruoc.cws.solver.ClueRankerPromptTextFactory;
import uk.co.mruoc.cws.usecase.ClueRanker;

@RequiredArgsConstructor
@Slf4j
public class BedrockClueRanker implements ClueRanker {

  private final PromptTextExecutor promptTextExecutor;
  private final ClueRankerPromptTextFactory promptTextFactory;
  private final ClueListConverter clueListConverter;

  public BedrockClueRanker(BedrockRuntimeClient client) {
    this(client, new DefaultBedrockConversationConfig());
  }

  public BedrockClueRanker(
      BedrockRuntimeClient client, BedrockConversationConfig conversationConfig) {
    this(new PromptTextExecutor(client, conversationConfig));
  }

  public BedrockClueRanker(PromptTextExecutor promptTextExecutor) {
    this(promptTextExecutor, new ClueRankerPromptTextFactory(), new ClueListConverter());
  }

  @Override
  public Clues rankByEase(Clues clues) {
    var promptText = promptTextFactory.toPromptText(clues);
    var rankedClueList = promptTextExecutor.execute(promptText);
    log.debug("ranked clue list {}", rankedClueList);
    var rankedIds = clueListConverter.toIds(rankedClueList);
    return clues.sortByIds(rankedIds);
  }
}
