package uk.co.mruoc.cws.usecase.attempt;

import java.util.UUID;
import lombok.Builder;
import uk.co.mruoc.cws.entity.Answer;
import uk.co.mruoc.cws.entity.Attempt;

@Builder
public class AttemptUpdater {

  private final AttemptFinder finder;
  private final AttemptRepository repository;

  public void saveAnswer(UUID attemptId, Answer answer) {
    var attempt = finder.findById(attemptId);
    var updatedAttempt = attempt.saveAnswer(answer);
    repository.save(updatedAttempt);
  }

  public void recordSolveStart(UUID attemptId) {
    var attempt = finder.findById(attemptId);
    saveAndReload(attempt.startSolve());
  }

  public void recordSolveEnd(UUID attemptId) {
    var attempt = finder.findById(attemptId);
    saveAndReload(attempt.endSolve());
  }

  private void saveAndReload(Attempt attempt) {
    repository.save(attempt);
  }
}
