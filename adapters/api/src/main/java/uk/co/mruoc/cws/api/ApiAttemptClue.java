package uk.co.mruoc.cws.api;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ApiAttemptClue {
  private final ApiClue clue;
  private final ApiAttemptClueAnswer answer;
}
