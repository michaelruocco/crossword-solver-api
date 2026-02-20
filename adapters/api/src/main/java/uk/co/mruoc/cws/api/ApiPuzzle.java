package uk.co.mruoc.cws.api;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ApiPuzzle {
  private final UUID id;
  private final String name;
  private final String hash;
  private final Instant createdAt;
  private final long attemptCount;
  private final ApiClues clues;
  private final ApiGrid grid;
}
