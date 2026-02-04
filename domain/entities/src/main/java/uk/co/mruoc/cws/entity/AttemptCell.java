package uk.co.mruoc.cws.entity;

public record AttemptCell(Cell cell, Character letter) {

  public AttemptCell(Cell cell) {
    this(cell, null);
  }

  public Coordinates coordinates() {
    return cell.coordinates();
  }
}
