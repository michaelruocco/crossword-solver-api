package uk.co.mruoc.cws.entity;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PuzzleSummary {
  private final UUID id;
  private final String name;
  private final Instant createdAt;
  private final long attemptCount;
}
