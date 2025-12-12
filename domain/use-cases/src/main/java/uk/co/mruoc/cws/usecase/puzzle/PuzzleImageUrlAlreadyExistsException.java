package uk.co.mruoc.cws.usecase.puzzle;

public class PuzzleImageUrlAlreadyExistsException extends RuntimeException {

  public PuzzleImageUrlAlreadyExistsException(long id, String hash) {
    super(String.format("a puzzle with id %d already exists for hash %s", id, hash));
  }
}
