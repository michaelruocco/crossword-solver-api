package uk.co.mruoc.cws.usecase.attempt;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import uk.co.mruoc.cws.entity.Attempt;

@Builder
@Slf4j
public class AttemptSolverRunnableFactory {

  private final AttemptSolver attemptSolver;
  private final AttemptRepository repository;

  public Runnable build(Attempt attempt) {
    return AttemptSolverRunnable.builder()
        .solver(attemptSolver)
        .repository(repository)
        .attemptId(attempt.id())
        .build();
  }
}
