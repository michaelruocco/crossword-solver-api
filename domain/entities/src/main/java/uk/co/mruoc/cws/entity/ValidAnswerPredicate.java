package uk.co.mruoc.cws.entity;

import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ValidAnswerPredicate implements Predicate<Answer> {

  private final Clues clues;

  public ValidAnswerPredicate(Clue clue) {
    this(new Clues(clue));
  }

  @Override
  public boolean test(Answer answer) {
    var clue = clues.findClue(answer.id()).orElseThrow();
    return isValid(clue, answer);
  }

  private boolean isValid(Clue clue, Answer answer) {
    var totalLength = clue.getTotalLength();
    if (answer.value().length() != totalLength) {
      return false;
    }
    var pattern = clue.pattern();
    return answer.matches(pattern);
  }
}
