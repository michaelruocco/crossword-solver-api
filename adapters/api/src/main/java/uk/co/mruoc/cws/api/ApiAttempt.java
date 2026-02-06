package uk.co.mruoc.cws.api;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ApiAttempt {
  private final UUID id;
  private final ApiPuzzle puzzle;
}
