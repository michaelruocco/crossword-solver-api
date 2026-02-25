package uk.co.mruoc.cws.usecase.attempt;

import java.util.UUID;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Builder
@Slf4j
public class AttemptSolverRunnableFactory {

  private final AttemptFinder finder;
  private final AttemptSolver solver;
  private final AttemptUpdater updater;

  public Runnable build(UUID attemptId) {
    return AttemptSolverRunnable.builder()
        .finder(finder)
        .solver(solver)
        .updater(updater)
        .attemptId(attemptId)
        .build();
  }
}
