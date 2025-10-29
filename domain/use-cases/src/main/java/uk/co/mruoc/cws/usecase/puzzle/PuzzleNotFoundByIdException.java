package uk.co.mruoc.cws.usecase.puzzle;

public class PuzzleNotFoundByIdException extends RuntimeException {

  public PuzzleNotFoundByIdException(Long id) {
    super(String.format("puzzle not found for id %d", id));
  }
}
