package uk.co.mruoc.cws.usecase.attempt;

import java.util.concurrent.Executor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import uk.co.mruoc.cws.entity.Attempt;

@Builder
@Slf4j
public class AsyncAttemptSolver {

  private final AttemptSolver attemptSolver;
  private final AttemptRepository repository;
  private final Executor executor;

  public void solve(Attempt attempt) {
    var runnable =
        AttemptSolverRunnable.builder()
            .solver(attemptSolver)
            .repository(repository)
            .attemptId(attempt.id())
            .build();
    executor.execute(runnable);
  }
}
