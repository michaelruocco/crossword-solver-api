package uk.co.mruoc.cws.usecase.attempt;

import lombok.Builder;
import uk.co.mruoc.cws.entity.Answer;
import uk.co.mruoc.cws.entity.Attempt;

@Builder
public class AttemptService {

  private final AttemptCreator creator;
  private final AttemptFinder finder;
  private final AttemptUpdater updater;
  private final AsyncAttemptSolver asyncSolver;

  public long createAttempt(long puzzleId) {
    return creator.create(puzzleId);
  }

  public void asyncSolveAttempt(long attemptId) {
    var attempt = findById(attemptId);
    asyncSolver.asyncSolve(attempt);
  }

  public void syncSolveAttempt(long attemptId) {
    var attempt = findById(attemptId);
    asyncSolver.syncSolve(attempt);
  }

  public Attempt findById(long id) {
    return finder.findById(id);
  }

  public void updateAnswer(long attemptId, Answer answer) {
    updater.saveAnswer(attemptId, answer);
  }
}
