package uk.co.mruoc.cws.api;

import java.util.Collection;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ApiGrid<C> {
  private final Collection<C> cells;
  private final int columnWidth;
  private final int rowHeight;
}
