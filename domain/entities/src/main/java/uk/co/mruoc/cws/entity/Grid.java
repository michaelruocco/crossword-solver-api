package uk.co.mruoc.cws.entity;

import java.util.Optional;
import java.util.function.UnaryOperator;

public record Grid(Cells cells, int columnWidth, int rowHeight) {

  public Cell findByCoordinates(Coordinates coordinates) {
    return cells.forceFindByCoordinates(coordinates);
  }

  public int numberOfColumns() {
    return cells.maxX();
  }

  public int numberOfRows() {
    return cells.maxY();
  }

  public int wordLength(Id id) {
    var idCell = cells.forceFindById(id.getId());
    var direction = id.getDirection();
    return countWhiteCellsFrom(idCell, direction) + 1;
  }

  private int countWhiteCellsFrom(Cell cell, Direction direction) {
    var increment = toIncrementFunction(direction);
    var nextCoordinates = increment.apply(cell.coordinates());
    var nextCell = cellAt(nextCoordinates);
    int count = 1;
    while (nextCell.isPresent() && !nextCell.get().black()) {
      nextCoordinates = increment.apply(cell.coordinates());
      nextCell = cellAt(nextCoordinates);
      count++;
    }
    return count;
  }

  private UnaryOperator<Coordinates> toIncrementFunction(Direction direction) {
    if (direction == Direction.ACROSS) {
      return Coordinates::incrementX;
    }
    return Coordinates::incrementY;
  }

  private Optional<Cell> cellAt(Coordinates coordinates) {
    return cells.findByCoordinates(coordinates);
  }
}
