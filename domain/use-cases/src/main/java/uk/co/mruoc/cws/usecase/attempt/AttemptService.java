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
  private final AttemptDeleter deleter;
  private final AsyncAttemptSolver asyncSolver;

  public UUID createAttempt(UUID puzzleId) {
    return creator.create(puzzleId);
  }

  public void asyncSolveAttempt(UUID attemptId) {
    finder.validateExistsById(attemptId);
    asyncSolver.asyncSolve(attemptId);
  }

  public void syncSolveAttempt(UUID attemptId) {
    finder.validateExistsById(attemptId);
    asyncSolver.syncSolve(attemptId);
  }

  public Attempt findById(UUID id) {
    return finder.findById(id);
  }

  public void updateAnswer(UUID attemptId, Answer answer) {
    updater.saveAnswer(attemptId, answer);
  }

  public void deleteAllAttempts(UUID puzzleId) {
    deleter.deleteAllAttempts(puzzleId);
  }
}
