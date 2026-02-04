package uk.co.mruoc.cws.entity;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AttemptCells implements Iterable<AttemptCell> {

  private final Collection<AttemptCell> values;

  @Override
  public Iterator<AttemptCell> iterator() {
    return values.iterator();
  }

  public Stream<AttemptCell> stream() {
    return values.stream();
  }

  public AttemptCells sort() {
    return new AttemptCells(
        values.stream()
            .sorted(Comparator.comparing(AttemptCell::coordinates, new CoordinatesComparator()))
            .toList());
  }
}
