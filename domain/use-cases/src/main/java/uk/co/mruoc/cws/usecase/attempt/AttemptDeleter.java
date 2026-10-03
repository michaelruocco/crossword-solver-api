package uk.co.mruoc.cws.usecase.attempt;

import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AttemptDeleter {

  private final AttemptRepository repository;

  public void deleteAllPuzzleAttempts(UUID puzzleId) {
    repository.deleteAllByPuzzleId(puzzleId);
  }

  public void deleteAllAttempts() {
    repository.deleteAll();
  }
}
