package uk.co.mruoc.cws.usecase.attempt;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Builder
@Slf4j
public class BacktrackingAttemptSolverRunnable implements Runnable {

  private final BacktrackingAttemptSolver solver;
  private final AttemptRepository repository;
  private final long attemptId;

  @Override
  public void run() {
    var attempt = repository.forceFindById(attemptId);
    log.info("solving attempt {}", attempt.id());
    var solvedAttempt = solver.solve(attempt);
    if (solvedAttempt.isPresent()) {
      log.info("solved attempt {}", solvedAttempt.get().asString());
      repository.save(solvedAttempt.get());
      return;
    }
    log.info("no solved attempt returned");
  }
}
