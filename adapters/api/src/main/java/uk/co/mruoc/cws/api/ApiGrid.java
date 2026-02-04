package uk.co.mruoc.cws.api;

import java.util.Collection;
import java.util.Comparator;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ApiGrid {
  private final Collection<ApiCell> cells;
  private final int columnWidth;
  private final int rowHeight;

  public Collection<ApiCell> getCells() {
    return cells.stream()
        .sorted(
            Comparator.comparing((ApiCell c) -> c.getCoordinates().y())
                .thenComparing(c -> c.getCoordinates().x()))
        .toList();
  }
}
