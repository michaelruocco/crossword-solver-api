package uk.co.mruoc.cws.api;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import uk.co.mruoc.cws.entity.Direction;

@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Data
public class ApiAnswer {
  private final int id;
  private final Direction direction;
  private final String value;
}
