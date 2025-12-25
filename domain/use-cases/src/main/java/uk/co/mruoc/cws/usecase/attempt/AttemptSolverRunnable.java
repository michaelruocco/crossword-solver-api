package uk.co.mruoc.cws.usecase.attempt;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Builder
@Slf4j
public class AttemptSolverRunnable implements Runnable {

  private final AttemptSolver solver;
  private final AttemptRepository repository;
  private final long attemptId;

  @Override
  public void run() {
    var attempt = repository.forceFindById(attemptId);
    var solvedAttempt = solver.solve(attempt);
    log.info("solved attempt {}", solvedAttempt.asString());
    repository.save(solvedAttempt);
  }
}
