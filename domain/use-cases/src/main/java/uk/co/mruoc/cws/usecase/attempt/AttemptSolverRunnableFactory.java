package uk.co.mruoc.cws.usecase.attempt;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import uk.co.mruoc.cws.entity.Attempt;

@Builder
@Slf4j
public class AttemptSolverRunnableFactory {

  private final AttemptFinder finder;
  private final AttemptSolver solver;
  private final AttemptRepository repository;

  public Runnable build(Attempt attempt) {
    return AttemptSolverRunnable.builder()
        .finder(finder)
        .solver(solver)
        .repository(repository)
        .attemptId(attempt.id())
        .build();
  }
}
