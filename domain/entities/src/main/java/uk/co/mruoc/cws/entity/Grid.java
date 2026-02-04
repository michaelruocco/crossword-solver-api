package uk.co.mruoc.cws.entity;

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
}
