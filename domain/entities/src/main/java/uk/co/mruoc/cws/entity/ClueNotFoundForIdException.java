package uk.co.mruoc.cws.entity;

public class ClueNotFoundForIdException extends RuntimeException {

  public ClueNotFoundForIdException(Id id, Attempt attempt) {
    super(
        String.format(
            "text not found for id %s in attempt %d on puzzle %d",
            id, attempt.id(), attempt.puzzleId()));
  }
}
