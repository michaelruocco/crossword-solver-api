package uk.co.mruoc.cws.usecase.attempt;

import java.util.Comparator;
import uk.co.mruoc.cws.entity.Candidates;

public class CandidateComparator implements Comparator<Candidates> {
  @Override
  public int compare(Candidates c1, Candidates c2) {
    return Comparator.comparingInt(Candidates::size)
        .thenComparingInt(Candidates::cluePatternCharCount)
        .reversed()
        .thenComparingInt(Candidates::bestScore)
        .reversed()
        .compare(c1, c2);
  }
}
