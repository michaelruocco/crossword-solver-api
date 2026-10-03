package uk.co.mruoc.cws.usecase.attempt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Comparator;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import uk.co.mruoc.cws.entity.Candidates;

class CandidateComparatorTest {

  private final Comparator<Candidates> comparator = new CandidateComparator();

  @Test
  void shouldSortOverallScoreDescending() {
    var c1 = mock(Candidates.class);
    when(c1.overallScore()).thenReturn(1d);
    var c2 = mock(Candidates.class);
    when(c2.overallScore()).thenReturn(2d);

    var sorted = Stream.of(c1, c2).sorted(comparator).toList();

    assertThat(sorted).containsExactly(c2, c1);
  }
}
