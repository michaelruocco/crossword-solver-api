package uk.co.mruoc.cws.api;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ApiAttempt {
  private final UUID id;
  private final Instant createdAt;
  private final boolean solving;
  private final ApiPuzzle puzzle;
  private final long answerCount;
}
