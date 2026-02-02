package uk.co.mruoc.cws.entity;

import java.util.Optional;

public record Cell(Coordinates coordinates, boolean black, Integer id) {

  public static Cell blackCell(Coordinates coordinates) {
    return new Cell(coordinates, true, null);
  }

  public static Cell whiteCell(Coordinates coordinates) {
    return new Cell(coordinates, false, null);
  }

  public static Cell idCell(Coordinates coordinates, Integer id) {
    return new Cell(coordinates, false, id);
  }

  public boolean hasId(int otherId) {
    return getId().map(id -> id == otherId).orElse(false);
  }

  public boolean hasId() {
    return getId().isPresent();
  }

  public int forceGetId() {
    return getId().orElseThrow();
  }

  public Optional<Integer> getId() {
    return Optional.ofNullable(id);
  }

  public int x() {
    return coordinates.x();
  }

  public int y() {
    return coordinates.y();
  }

  public boolean hasCoordinates(Coordinates otherCoordinates) {
    return this.coordinates.equals(otherCoordinates);
  }
}
