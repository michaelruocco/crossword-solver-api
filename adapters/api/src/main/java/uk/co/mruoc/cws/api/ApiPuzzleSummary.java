package uk.co.mruoc.cws.api;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ApiPuzzleSummary {
  private final UUID id;
  private final String name;
  private final long attemptCount;
}
