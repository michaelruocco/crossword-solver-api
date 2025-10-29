package uk.co.mruoc.cws.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Data
@Slf4j
public class Id {

  private final int id;
  private final Direction direction;

  public static Id across(int id) {
    return new Id(id, Direction.ACROSS);
  }

  public static Id down(int id) {
    return new Id(id, Direction.DOWN);
  }

  public Id(String value) {
    this(toNumericId(value), toDirection(value));
  }

  @Override
  public String toString() {
    return String.format("%d%s", id, direction.getId());
  }

  private static int toNumericId(String value) {
    try {
      var digits = value.replaceAll("[^0-9]", "");
      log.debug("parsing numeric id using numbers {} from {}", digits, value);
      return Integer.parseInt(digits);
    } catch (NumberFormatException e) {
      throw new IdParseException(value, e);
    }
  }

  private static Direction toDirection(String value) {
    var letters = value.replaceAll("[^A-Z]", "");
    log.debug("parsing direction id using letters {} from {}", letters, value);
    return Direction.toDirection(letters.charAt(0));
  }
}
