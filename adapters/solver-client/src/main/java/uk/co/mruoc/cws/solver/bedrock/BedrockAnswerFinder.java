package uk.co.mruoc.cws.solver.bedrock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import uk.co.mruoc.cws.entity.Candidates;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.solver.DelegatingFindAnswerPromptTextFactory;
import uk.co.mruoc.cws.solver.FindAnswerPromptTextFactory;
import uk.co.mruoc.cws.solver.FindAnswerResponseConverter;
import uk.co.mruoc.cws.usecase.AnswerFinder;

@Slf4j
@RequiredArgsConstructor
public class BedrockAnswerFinder implements AnswerFinder {

  private final PromptTextExecutor promptTextExecutor;
  private final FindAnswerPromptTextFactory promptTextFactory;
  private final FindAnswerResponseConverter responseConverter;

  public BedrockAnswerFinder(BedrockRuntimeClient client) {
    this(
        client,
        new DefaultBedrockConversationConfig(),
        new DefaultBedrockModelConfig().answerFinderId());
  }

  public BedrockAnswerFinder(
      BedrockRuntimeClient client, BedrockConversationConfig conversationConfig, String modelId) {
    this(new PromptTextExecutor(client, conversationConfig, modelId));
  }

  public BedrockAnswerFinder(PromptTextExecutor promptTextExecutor) {
    this(
        promptTextExecutor,
        new DelegatingFindAnswerPromptTextFactory(),
        new FindAnswerResponseConverter());
  }

  @Override
  public Candidates findCandidates(Clue clue, int numberOfCandidates) {
    var promptText = promptTextFactory.toPromptText(clue, numberOfCandidates);
    var responseText = promptTextExecutor.execute(promptText);
    return new Candidates(clue, responseConverter.toCandidates(responseText)).validAnswers(clue);
  }
}
