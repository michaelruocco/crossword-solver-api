package uk.co.mruoc.cws.usecase;

import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import uk.co.mruoc.cws.entity.Answers;
import uk.co.mruoc.cws.entity.Attempt;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.entity.Intersection;
import uk.co.mruoc.cws.entity.Word;

@Slf4j
public class PatternFactory {

  public String build(Clue clue, Attempt attempt) {
    var pattern = new StringBuilder();
    pattern.repeat('?', clue.getTotalLength());
    var answers = attempt.answers();
    var intersections = attempt.getIntersections(clue.id());
    for (var intersection : intersections) {
      int index = intersection.getIntersectingIndex(clue.direction());
      var id = intersection.getId(clue.direction());
      var letter = answers.getLetterOfConfirmedAnswer(id, index);
      log.debug(
          "got index {} letter {} for clue {} using intersection {}",
          index,
          letter,
          id,
          intersection);
      letter.ifPresent(l -> pattern.setCharAt(intersection.getIndex(clue.direction()), l));
    }
    return pattern.toString();
  }

  public String build(Word word, Collection<Intersection> intersections, Answers answers) {
    var pattern = new StringBuilder();
    pattern.repeat('?', word.getLength());
    for (var intersection : intersections) {
      int index = intersection.getIntersectingIndex(word.getDirection());
      var id = intersection.getId(word.getDirection());
      var letter = answers.getLetterOfConfirmedAnswer(id, index);
      log.debug(
          "got index {} letter {} for word {} using intersection {}",
          index,
          letter,
          id,
          intersection);
      letter.ifPresent(l -> pattern.setCharAt(intersection.getIndex(word.getDirection()), l));
    }
    return pattern.toString();
  }
}
