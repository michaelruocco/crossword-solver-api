package uk.co.mruoc.cws.solver.stub;

import lombok.RequiredArgsConstructor;
import uk.co.mruoc.cws.entity.Answer;
import uk.co.mruoc.cws.entity.Candidates;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.usecase.AnswerFinder;

@RequiredArgsConstructor
public class FakeAnswerFinder implements AnswerFinder {

  private final FakeAnswers answers;

  @Override
  public Candidates findCandidates(Clue clue, int numberOfCandidates) {
    return new Candidates(clue, findAnswer(clue));
  }

  private Answer findAnswer(Clue clue) {
    return answers.getAnswer(clue);
  }
}
