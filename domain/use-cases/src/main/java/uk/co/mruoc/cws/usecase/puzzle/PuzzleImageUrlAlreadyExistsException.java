package uk.co.mruoc.cws.usecase.puzzle;

public class PuzzleImageUrlAlreadyExistsException extends RuntimeException {

  public PuzzleImageUrlAlreadyExistsException(long id, String imageUrl) {
    super(String.format("a puzzle with id %d already exists for image url %s", id, imageUrl));
  }
}
