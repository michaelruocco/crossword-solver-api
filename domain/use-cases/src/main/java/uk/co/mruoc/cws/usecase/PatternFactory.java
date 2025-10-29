package uk.co.mruoc.cws.usecase;

import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import uk.co.mruoc.cws.entity.Answers;
import uk.co.mruoc.cws.entity.Intersection;
import uk.co.mruoc.cws.entity.Word;

@Slf4j
public class PatternFactory {

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
