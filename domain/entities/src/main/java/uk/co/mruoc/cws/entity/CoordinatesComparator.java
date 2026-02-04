package uk.co.mruoc.cws.entity;

import java.util.Comparator;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CoordinatesComparator implements Comparator<Coordinates> {

  private final Comparator<Coordinates> comparator;

  public CoordinatesComparator() {
    this(Comparator.comparing(Coordinates::y).thenComparing(Coordinates::x));
  }

  @Override
  public int compare(Coordinates c1, Coordinates c2) {
    return comparator.compare(c1, c2);
  }
}
