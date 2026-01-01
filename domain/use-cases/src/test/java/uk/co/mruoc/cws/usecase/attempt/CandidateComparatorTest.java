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
  void shouldSortBySizeAscending() {
    var c1 = mock(Candidates.class);
    when(c1.size()).thenReturn(2);
    var c2 = mock(Candidates.class);
    when(c2.size()).thenReturn(1);

    var sorted = Stream.of(c1, c2).sorted(comparator).toList();

    assertThat(sorted).containsExactly(c2, c1);
  }

  @Test
  void shouldSortByPatternCharDescendingIfSameSize() {
    var c1 = mock(Candidates.class);
    when(c1.size()).thenReturn(1);
    when(c1.cluePatternCharCount()).thenReturn(2);
    var c2 = mock(Candidates.class);
    when(c2.size()).thenReturn(1);
    when(c2.cluePatternCharCount()).thenReturn(1);

    var sorted = Stream.of(c1, c2).sorted(comparator).toList();

    assertThat(sorted).containsExactly(c2, c1);
  }

  @Test
  void shouldSortByBestScoreDescendingIfSizeAndPatternCharCountSame() {
    var c1 = mock(Candidates.class);
    when(c1.size()).thenReturn(1);
    when(c1.cluePatternCharCount()).thenReturn(1);
    when(c1.bestScore()).thenReturn(80);
    var c2 = mock(Candidates.class);
    when(c2.size()).thenReturn(1);
    when(c2.cluePatternCharCount()).thenReturn(1);
    when(c2.bestScore()).thenReturn(90);

    var sorted = Stream.of(c1, c2).sorted(comparator).toList();

    assertThat(sorted).containsExactly(c2, c1);
  }
}
