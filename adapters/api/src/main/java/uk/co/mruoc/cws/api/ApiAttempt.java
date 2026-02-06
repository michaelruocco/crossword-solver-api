package uk.co.mruoc.cws.api;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class ApiAttempt {
  private final UUID id;
  private final ApiPuzzle puzzle;
}
