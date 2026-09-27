package uk.co.mruoc.cws.usecase.puzzle;

import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PuzzleDeleter {

  private final PuzzleRepository repository;

  public void delete(UUID puzzleId) {
    repository.deleteById(puzzleId);
  }
}
