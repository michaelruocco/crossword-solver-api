package uk.co.mruoc.cws.usecase.attempt;

import java.util.Optional;
import uk.co.mruoc.cws.entity.Attempt;
import uk.co.mruoc.cws.entity.Id;
import uk.co.mruoc.cws.entity.Intersection;

public class ConstraintPropagator {

  public Optional<Attempt> propagate(Attempt attempt, Id confirmedId) {
    Attempt current = attempt;

    for (var intersection : attempt.getIntersections(confirmedId)) {
      current = propagateIntersection(current, confirmedId, intersection);
      if (current == null) {
        return Optional.empty();
      }
    }

    return Optional.of(current);
  }

  private Attempt propagateIntersection(Attempt attempt, Id id, Intersection intersection) {
    var confirmedAnswer = attempt.forceGetConfirmedAnswer(id);
    var confirmedDirection = id.getDirection();
    int confirmedIndex = intersection.getIndex(confirmedDirection);

    var letterOpt = confirmedAnswer.letterAt(confirmedIndex);
    if (letterOpt.isEmpty()) {
      return attempt;
    }

    char letter = letterOpt.get();
    var targetId = intersection.getId(confirmedDirection);
    var targetClue = attempt.getClue(targetId);
    int index = intersection.getIntersectingIndex(confirmedDirection);
    var updatedClue = targetClue.setPatternLetter(index, letter);
    var updatedAttempt = attempt.updateClue(updatedClue);
    var confirmedValid = updatedAttempt.getConfirmedValidAnswers();

    if (confirmedValid.size() < updatedAttempt.getConfirmedAnswers().size()) {
      return null;
    }
    return updatedAttempt.withAnswers(confirmedValid);
  }
}
