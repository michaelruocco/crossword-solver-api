package uk.co.mruoc.cws.usecase;

import lombok.extern.slf4j.Slf4j;
import uk.co.mruoc.cws.entity.Attempt;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.entity.Clues;

@Slf4j
public class PatternFactory {

  public Attempt addPatternsToClues(Attempt attempt) {
    var updatedClues = addPatternsToClues(attempt.getClues(), attempt);
    return attempt.withClues(updatedClues);
  }

  public Clues addPatternsToClues(Clues clues, Attempt attempt) {
    return new Clues(clues.stream().map(clue -> addPatternToClue(clue, attempt)).toList());
  }

  public Clue addPatternToClue(Clue clue, Attempt attempt) {
    var pattern = build(clue, attempt);
    return clue.withPattern(pattern);
  }

  public String build(Clue clue, Attempt attempt) {
    var pattern = new StringBuilder();
    pattern.repeat('?', clue.totalLength());
    var answers = attempt.answers();
    var intersections = attempt.getIntersections(clue.id());
    for (var intersection : intersections) {
      int index = intersection.toIntersectingIndex(clue.direction());
      var id = intersection.toIntersectingId(clue.direction());
      var letter = answers.getLetterOfConfirmedAnswer(id, index);
      log.debug(
          "got index {} letter {} for clue {} using intersection {}",
          index,
          letter,
          id,
          intersection);
      letter.ifPresent(l -> pattern.setCharAt(intersection.toIndex(clue.direction()), l));
    }
    return pattern.toString();
  }
}
