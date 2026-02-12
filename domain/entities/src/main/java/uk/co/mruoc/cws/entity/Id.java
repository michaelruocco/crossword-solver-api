package uk.co.mruoc.cws.entity;

import java.util.Objects;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Data
@Slf4j
public class Id {

  private final int number;
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
    if (Objects.isNull(direction)) {
      return Integer.toString(number);
    }
    return String.format("%d%s", number, direction.getId());
  }

  private static int toNumericId(String value) {
    try {
      var digits = value.replaceAll("\\D", "");
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
