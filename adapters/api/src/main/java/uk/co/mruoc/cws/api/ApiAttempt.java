package uk.co.mruoc.cws.api;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ApiAttempt {
  private final long id;
  private final ApiPuzzle<ApiAttemptClue, ApiAttemptCell> puzzle;
}
