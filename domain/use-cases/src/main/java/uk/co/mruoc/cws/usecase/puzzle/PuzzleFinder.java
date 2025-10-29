package uk.co.mruoc.cws.usecase.puzzle;

import lombok.RequiredArgsConstructor;
import uk.co.mruoc.cws.entity.Puzzle;

@RequiredArgsConstructor
public class PuzzleFinder {

  private final PuzzleRepository repository;

  public Puzzle findById(Long id) {
    return repository.findById(id).orElseThrow(() -> new PuzzleNotFoundByIdException(id));
  }
}
