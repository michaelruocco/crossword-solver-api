package uk.co.mruoc.cws.entity;

public class ClueNotFoundForIdException extends RuntimeException {

  public ClueNotFoundForIdException(Id id) {
    super(String.format("clue not found for id %s", id));
  }

  public ClueNotFoundForIdException(Id id, Attempt attempt) {
    super(
        String.format(
            "clue not found for id %s in attempt %s on puzzle %s",
            id, attempt.id(), attempt.puzzleId()));
  }
}
