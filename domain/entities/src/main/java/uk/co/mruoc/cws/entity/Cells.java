package uk.co.mruoc.cws.entity;

import java.util.Collection;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Cells {

  private final Collection<Cell> values;

  public Coordinates forceFindCoordinatesById(int id) {
    return findById(id).map(Cell::coordinates).orElseThrow();
  }

  public Optional<Cell> findById(int id) {
    return values.stream().filter(cell -> cell.number() == id).findFirst();
  }
}
