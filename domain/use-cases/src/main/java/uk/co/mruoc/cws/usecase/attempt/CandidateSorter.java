package uk.co.mruoc.cws.usecase.attempt;

import java.util.Collection;
import java.util.Comparator;
import lombok.RequiredArgsConstructor;
import uk.co.mruoc.cws.entity.Attempt;
import uk.co.mruoc.cws.entity.Candidates;

@RequiredArgsConstructor
public class CandidateSorter {

  private final Comparator<SortableCandidates> comparator;

  public CandidateSorter() {
    this(buildComparator());
  }

  public Collection<Candidates> sort(Attempt attempt, Collection<Candidates> candidates) {
    return candidates.stream()
        .map(
            c ->
                new SortableCandidates(
                    c, attempt.calculateIntersectingCharsPopulatedPercentage(c.clue())))
        .sorted(comparator)
        .map(SortableCandidates::candidates)
        .toList();
  }

  private static Comparator<SortableCandidates> buildComparator() {
    return Comparator.comparingDouble(SortableCandidates::intersectingCharsPopulatedPercentage)
        .reversed()
        .thenComparing(c -> c.candidates, new CandidateComparator());
  }

  private record SortableCandidates(
      Candidates candidates, double intersectingCharsPopulatedPercentage) {}
}
