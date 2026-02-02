package uk.co.mruoc.cws.entity;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
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

  public Cell forceFindByCoordinates(Coordinates coordinates) {
    return findByCoordinates(coordinates).orElseThrow();
  }

  public Optional<Cell> findByCoordinates(Coordinates coordinates) {
    return findBy(cell -> cell.hasCoordinates(coordinates));
  }

  public Coordinates forceFindCoordinatesById(int id) {
    return forceFindById(id).coordinates();
  }

  public Cell forceFindById(int id) {
    return findById(id).orElseThrow();
  }

  public Optional<Cell> findById(int id) {
    return findBy(cell -> cell.hasId(id));
  }

  public Optional<Cell> findBy(Predicate<Cell> predicate) {
    return values.stream().filter(predicate).findFirst();
  }

  public int maxX() {
    return toMax(Cell::x);
  }

  public int maxY() {
    return toMax(Cell::y);
  }

  private int toMax(Function<Cell, Integer> function) {
    return values.stream().map(function).max(Comparator.naturalOrder()).orElse(0);
  }
}
