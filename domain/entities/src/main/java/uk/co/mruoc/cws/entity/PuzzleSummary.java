package uk.co.mruoc.cws.entity;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PuzzleSummary {
  private final UUID id;
  private final String name;
  private final long attemptCount;
}
