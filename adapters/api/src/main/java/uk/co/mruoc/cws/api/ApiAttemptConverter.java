package uk.co.mruoc.cws.api;

import lombok.RequiredArgsConstructor;
import uk.co.mruoc.cws.entity.Attempt;

@RequiredArgsConstructor
public class ApiAttemptConverter {

  private final ApiPuzzleConverter puzzleConverter;

  public ApiAttemptConverter() {
    this(new ApiPuzzleConverter());
  }

  public ApiAttempt toApiAttempt(Attempt attempt) {
    return ApiAttempt.builder()
        .id(attempt.id())
        .puzzle(puzzleConverter.toApiPuzzle(attempt))
        .build();
  }
}
