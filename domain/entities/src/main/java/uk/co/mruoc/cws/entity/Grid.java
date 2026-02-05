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

  public Cells getWordCells(Id id) {
    return cells.getWordCells(id);
  }

  public Grid withCells(Cells cells) {
    return new Grid(cells, columnWidth, rowHeight);
  }
}
