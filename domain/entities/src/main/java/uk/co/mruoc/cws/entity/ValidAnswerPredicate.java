package uk.co.mruoc.cws.entity;

import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ValidAnswerPredicate implements Predicate<Answer> {

  private final Clues clues;

  public ValidAnswerPredicate(Clue clue) {
    this(new Clues(clue));
  }

  @Override
  public boolean test(Answer answer) {
    var clue = clues.forceFind(answer.id());
    var valid = isValid(clue, answer);
    log.debug(
        "is answer {} valid {} for clue {} {} with pattern {}",
        answer.value(),
        valid,
        clue.id(),
        clue.text(),
        clue.pattern());
    return valid;
  }

  private boolean isValid(Clue clue, Answer answer) {
    var totalLength = clue.totalLength();
    if (answer.value().length() != totalLength) {
      return false;
    }
    var pattern = clue.pattern();
    return answer.matches(pattern);
  }
}
