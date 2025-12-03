package uk.co.mruoc.cws.entity;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Cells implements Iterable<Cell> {

  private final Collection<Cell> values;

  public Cells(Cell... values) {
    this(List.of(values));
  }

  @Override
  public Iterator<Cell> iterator() {
    return values.iterator();
  }

  public Coordinates forceFindCoordinatesById(int id) {
    return findById(id).map(Cell::coordinates).orElseThrow();
  }

  public Optional<Cell> findById(int id) {
    return values.stream().filter(cell -> cell.id() == id).findFirst();
  }
}
