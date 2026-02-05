package uk.co.mruoc.cws.entity;

import java.util.Optional;

public record Cell(Coordinates coordinates, boolean black, Integer id, Character letter) {

  public static Cell blackCell(Coordinates coordinates) {
    return new Cell(coordinates, true, null, null);
  }

  public static Cell whiteCell(Coordinates coordinates) {
    return new Cell(coordinates, false, null, null);
  }

  public static Cell idCell(Coordinates coordinates, Integer id) {
    return new Cell(coordinates, false, id, null);
  }

  public Cell(Coordinates coordinates, boolean black, Integer id) {
    this(coordinates, black, id, null);
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

  public Cell withLetter(Character letter) {
    return new Cell(coordinates, black, id, letter);
  }

  public Optional<Integer> getId() {
    return Optional.ofNullable(id);
  }

  public Optional<Character> getLetter() {
    return Optional.ofNullable(letter);
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
