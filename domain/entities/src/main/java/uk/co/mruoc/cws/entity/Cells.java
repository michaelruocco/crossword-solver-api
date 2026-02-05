package uk.co.mruoc.cws.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.IterableUtils;

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

  public Stream<Cell> stream() {
    return values.stream();
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

  public int size() {
    return values.size();
  }

  public Cells sort() {
    return new Cells(
        values.stream()
            .sorted(Comparator.comparing(Cell::coordinates, new CoordinatesComparator()))
            .toList());
  }

  public Cells populateLetters(Answer answer) {
    var cells = getWordCells(answer.id());
    return new Cells(
        IntStream.range(0, cells.size()).mapToObj(i -> populateLetter(answer, cells, i)).toList());
  }

  public Cells getWordCells(Id id) {
    var idCell = forceFindById(id.getId());
    var direction = id.getDirection();
    return getWhiteCellsFrom(idCell, direction);
  }

  private Cells getWhiteCellsFrom(Cell cell, Direction direction) {
    var whiteCells = new ArrayList<Cell>();
    whiteCells.add(cell);
    var increment = toIncrementFunction(direction);
    var nextCoordinates = increment.apply(cell.coordinates());
    var nextCell = findByCoordinates(nextCoordinates);
    while (nextCell.isPresent() && !nextCell.get().black()) {
      whiteCells.add(nextCell.get());
      nextCoordinates = increment.apply(nextCoordinates);
      nextCell = findByCoordinates(nextCoordinates);
    }
    return new Cells(whiteCells);
  }

  private UnaryOperator<Coordinates> toIncrementFunction(Direction direction) {
    if (direction == Direction.ACROSS) {
      return Coordinates::incrementX;
    }
    return Coordinates::decrementY;
  }

  private int toMax(Function<Cell, Integer> function) {
    return values.stream().map(function).max(Comparator.naturalOrder()).orElse(0);
  }

  private Cell populateLetter(Answer answer, Cells cells, int i) {
    var cell = IterableUtils.get(cells, i);
    return answer.letterAt(i).map(cell::withLetter).orElse(cell);
  }
}
