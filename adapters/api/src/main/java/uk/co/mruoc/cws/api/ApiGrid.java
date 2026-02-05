package uk.co.mruoc.cws.api;

import java.util.Collection;
import lombok.Builder;
import lombok.Data;
import lombok.With;

@Builder
@Data
public class ApiGrid {
  @With private final Collection<ApiCell> cells;
  private final int columnWidth;
  private final int rowHeight;
}
