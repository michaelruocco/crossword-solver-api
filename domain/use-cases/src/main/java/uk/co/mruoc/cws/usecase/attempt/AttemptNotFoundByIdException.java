package uk.co.mruoc.cws.usecase.attempt;

public class AttemptNotFoundByIdException extends RuntimeException {

  public AttemptNotFoundByIdException(long id) {
    super(String.format("attempt not found for id %d", id));
  }
}
