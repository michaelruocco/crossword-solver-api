package uk.co.mruoc.cws.usecase.attempt;

import java.util.UUID;

public class AttemptNotFoundByIdException extends RuntimeException {

  public AttemptNotFoundByIdException(UUID id) {
    super(String.format("attempt not found for id %s", id));
  }
}
