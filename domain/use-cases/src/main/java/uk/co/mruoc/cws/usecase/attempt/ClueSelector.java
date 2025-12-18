package uk.co.mruoc.cws.usecase.attempt;

import java.util.Comparator;
import lombok.RequiredArgsConstructor;
import uk.co.mruoc.cws.entity.Attempt;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.usecase.PatternFactory;

@RequiredArgsConstructor
public class ClueSelector {

  private final PatternFactory patternFactory;

  public ClueSelector() {
    this(new PatternFactory());
  }

  public Clue selectNextClue(Attempt attempt) {
    return attempt.getCluesWithUnconfirmedAnswer().stream()
        .map(clue -> clue.withPattern(patternFactory.build(clue, attempt)))
        .max(
            Comparator.comparingInt((Clue c) -> attempt.getIntersectingIds(c.id()).size())
                .reversed()
                .thenComparingInt(Clue::getPatternCharCount))
        .orElseThrow();
  }
}
