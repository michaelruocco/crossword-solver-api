package uk.co.mruoc.cws.usecase.attempt;

import lombok.Builder;
import uk.co.mruoc.cws.entity.Answer;
import uk.co.mruoc.cws.entity.Attempt;

@Builder
public class AttemptService {

  private final AttemptCreator creator;
  private final AttemptFinder finder;
  private final AttemptUpdater updater;
  private final AttemptSolver solver;

  public long createAttempt(long puzzleId) {
    return creator.create(puzzleId);
  }

  public void solveAttempt(long attemptId) {
    var attempt = findById(attemptId);
    solver.solve(attempt);
  }

  public Attempt findById(long id) {
    return finder.findById(id);
  }

  public void updateAnswer(long attemptId, Answer answer) {
    updater.saveAnswer(attemptId, answer);
  }
}
