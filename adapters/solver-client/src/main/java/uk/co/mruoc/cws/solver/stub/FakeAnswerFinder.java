package uk.co.mruoc.cws.solver.stub;

import lombok.RequiredArgsConstructor;
import uk.co.mruoc.cws.entity.Answer;
import uk.co.mruoc.cws.entity.Answers;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.entity.Clues;
import uk.co.mruoc.cws.usecase.AnswerFinder;

@RequiredArgsConstructor
public class FakeAnswerFinder implements AnswerFinder {

  private final FakeAnswers answers;

  @Override
  public Answers findAnswers(Clues clues) {
    return new Answers(clues.stream().map(this::findAnswer).toList());
  }

  @Override
  public Answer findAnswer(Clue clue) {
    return answers.getAnswer(clue);
  }
}
