package uk.co.mruoc.cws.usecase.attempt;

import java.util.UUID;
import java.util.concurrent.Executor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Builder
@Slf4j
public class AsyncAttemptSolver {

  private final AttemptUpdater updater;
  private final AttemptSolverRunnableFactory runnableFactory;
  private final Executor executor;

  public void asyncSolve(UUID attemptId) {
    updater.recordSolveStart(attemptId);
    var runnable = runnableFactory.build(attemptId);
    executor.execute(runnable);
  }

  public void syncSolve(UUID attemptId) {
    var runnable = runnableFactory.build(attemptId);
    runnable.run();
  }
}
