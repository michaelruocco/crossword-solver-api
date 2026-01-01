package uk.co.mruoc.cws.usecase.attempt;

import java.util.concurrent.Executor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import uk.co.mruoc.cws.entity.Attempt;

@Builder
@Slf4j
public class AsyncAttemptSolver {

  private final AttemptSolverRunnableFactory runnableFactory;
  private final Executor executor;

  public void asyncSolve(Attempt attempt) {
    var runnable = runnableFactory.build(attempt);
    executor.execute(runnable);
  }

  public void syncSolve(Attempt attempt) {
    var runnable = runnableFactory.build(attempt);
    runnable.run();
  }
}
