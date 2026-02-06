package uk.co.mruoc.cws.usecase.attempt;

import java.util.UUID;
import lombok.Builder;
import uk.co.mruoc.cws.entity.Answer;
import uk.co.mruoc.cws.entity.Attempt;

@Builder
public class AttemptService {

  private final AttemptCreator creator;
  private final AttemptFinder finder;
  private final AttemptUpdater updater;
  private final AsyncAttemptSolver asyncSolver;

  public UUID createAttempt(UUID puzzleId) {
    return creator.create(puzzleId);
  }

  public void asyncSolveAttempt(UUID attemptId) {
    var attempt = findById(attemptId);
    asyncSolver.asyncSolve(attempt);
  }

  public void syncSolveAttempt(UUID attemptId) {
    var attempt = findById(attemptId);
    asyncSolver.syncSolve(attempt);
  }

  public Attempt findById(UUID id) {
    return finder.findById(id);
  }

  public void updateAnswer(UUID attemptId, Answer answer) {
    updater.saveAnswer(attemptId, answer);
  }
}
