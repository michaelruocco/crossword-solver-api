package uk.co.mruoc.cws.entity;

import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ValidAnswerPredicate implements Predicate<Answer> {

  private final Clues clues;

  @Override
  public boolean test(Answer answer) {
    return clues.findClue(answer.id()).filter(value -> isValid(value, answer)).isPresent();
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
