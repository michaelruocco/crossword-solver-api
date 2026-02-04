package uk.co.mruoc.cws.api;

import java.util.Collection;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ApiPuzzle<T> {
  private final long id;
  private final String name;
  private final String hash;
  private final Collection<T> clues;
  private final ApiGrid grid;
}
