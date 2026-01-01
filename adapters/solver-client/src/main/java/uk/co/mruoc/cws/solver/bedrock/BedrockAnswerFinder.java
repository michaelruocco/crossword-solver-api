package uk.co.mruoc.cws.solver.bedrock;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.IterableUtils;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import uk.co.mruoc.cws.entity.Answer;
import uk.co.mruoc.cws.entity.Answers;
import uk.co.mruoc.cws.entity.Candidates;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.entity.Clues;
import uk.co.mruoc.cws.solver.FindAnswerPromptTextFactory;
import uk.co.mruoc.cws.solver.FindAnswerResponseConverter;
import uk.co.mruoc.cws.usecase.AnswerFinder;

@RequiredArgsConstructor
public class BedrockAnswerFinder implements AnswerFinder {

  private final PromptTextExecutor promptTextExecutor;
  private final FindAnswerPromptTextFactory promptTextFactory;
  private final FindAnswerResponseConverter responseConverter;

  public BedrockAnswerFinder(BedrockRuntimeClient client) {
    this(client, new DefaultBedrockConversationConfig());
  }

  public BedrockAnswerFinder(
      BedrockRuntimeClient client, BedrockConversationConfig conversationConfig) {
    this(new PromptTextExecutor(client, conversationConfig));
  }

  public BedrockAnswerFinder(PromptTextExecutor promptTextExecutor) {
    this(promptTextExecutor, new FindAnswerPromptTextFactory(), new FindAnswerResponseConverter());
  }

  @Override
  public Candidates findCandidates(Clue clue, int numberOfCandidates) {
    var promptText = promptTextFactory.toPromptText(clue, numberOfCandidates);
    var responseText = promptTextExecutor.execute(promptText);
    return new Candidates(clue, responseConverter.toCandidates(responseText)).validAnswers(clue);
  }

  @Override
  public Answers findAnswers(Clues clues) {
    if (clues.size() == 1) {
      return new Answers(findAnswer(IterableUtils.get(clues, 0)));
    }
    var promptText = promptTextFactory.toPromptText(clues);
    var responseText = promptTextExecutor.execute(promptText);
    return new Answers(responseConverter.toAnswers(responseText)).validAnswers(clues);
  }

  @Override
  public Answer findAnswer(Clue clue) {
    var promptText = promptTextFactory.toPromptText(clue);
    var responseText = promptTextExecutor.execute(promptText);
    return responseConverter.toAnswer(responseText);
  }
}
