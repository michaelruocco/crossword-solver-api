package uk.co.mruoc.cws.api;

import java.util.Collection;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ApiPuzzle<C, GC> {
  private final long id;
  private final String name;
  private final String hash;
  private final Collection<C> clues;
  private final ApiGrid<GC> grid;
}
