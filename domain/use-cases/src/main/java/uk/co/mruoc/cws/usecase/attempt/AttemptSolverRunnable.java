package uk.co.mruoc.cws.usecase.attempt;

import java.util.UUID;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Builder
@Slf4j
public class AttemptSolverRunnable implements Runnable {

  private final AttemptFinder finder;
  private final AttemptUpdater updater;
  private final AttemptSolver solver;
  private final UUID attemptId;

  @Override
  public void run() {
    try {
      updater.recordSolveStart(attemptId);
      var attempt = finder.findById(attemptId);
      var solvedAttempt = solver.solve(attempt);
      log.info("solve attempt ended with {}", solvedAttempt.asString());
    } finally {
      updater.recordSolveEnd(attemptId);
    }
  }
}
