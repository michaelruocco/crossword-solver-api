package uk.co.mruoc.cws.usecase.puzzle;

import java.util.UUID;

public class PuzzleNotFoundByIdException extends RuntimeException {

  public PuzzleNotFoundByIdException(UUID id) {
    super(String.format("puzzle not found for id %s", id));
  }
}
