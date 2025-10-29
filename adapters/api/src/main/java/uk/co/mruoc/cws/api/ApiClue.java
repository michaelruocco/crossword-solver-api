package uk.co.mruoc.cws.api;

import java.util.Collection;
import lombok.Builder;
import lombok.Data;
import uk.co.mruoc.cws.entity.Coordinates;
import uk.co.mruoc.cws.entity.Direction;

@Builder
@Data
public class ApiClue {
  private final int id;
  private final Coordinates coordinates;
  private final Direction direction;
  private final String text;
  private final Collection<Integer> lengths;
}
