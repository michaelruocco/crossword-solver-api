package uk.co.mruoc.cws.entity;

public record Coordinates(int x, int y) {

  public Coordinates incrementX() {
    return new Coordinates(x + 1, y);
  }

  public Coordinates decrementY() {
    return new Coordinates(x, y - 1);
  }
}
