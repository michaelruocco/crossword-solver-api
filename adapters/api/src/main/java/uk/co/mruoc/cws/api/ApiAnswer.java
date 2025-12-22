package uk.co.mruoc.cws.api;

import lombok.Data;
import uk.co.mruoc.cws.entity.Direction;

@Data
public class ApiAnswer {
  private int id;
  private Direction direction;
  private String value;
}
