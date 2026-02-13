package uk.co.mruoc.cws.usecase.attempt;

import uk.co.mruoc.cws.entity.Attempt;
import uk.co.mruoc.cws.entity.Clue;

public class NoIntersectingIdsToRetryException extends SolverException {

  public NoIntersectingIdsToRetryException(Attempt attempt, Clue clue) {
    super(
        String.format(
            "no intersecting ids to retry for clue %s on attempt %s",
            clue.id().toString(), attempt.id()));
  }
}
