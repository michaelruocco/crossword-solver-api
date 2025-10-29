package uk.co.mruoc.cws.entity;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Direction {
  ACROSS('A'),
  DOWN('D');

  private final char id;

  public boolean hasId(char otherId) {
    return id == otherId;
  }

  public static Direction toDirection(char idToFind) {
    return Arrays.stream(Direction.values())
        .filter(d -> d.hasId(idToFind))
        .findFirst()
        .orElseThrow(() -> new DirectionIdNotSupportedException(idToFind));
  }
}
