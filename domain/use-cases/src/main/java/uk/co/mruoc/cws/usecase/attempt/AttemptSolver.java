package uk.co.mruoc.cws.usecase.attempt;

import java.time.Duration;
import java.util.concurrent.Executor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import uk.co.mruoc.cws.entity.Attempt;
import uk.co.mruoc.cws.usecase.AnswerFinder;
import uk.co.mruoc.cws.usecase.PatternFactory;

@Builder
@Slf4j
public class AttemptSolver {

  private final AnswerFinder answerFinder;
  private final AttemptRepository repository;
  private final PatternFactory patternFactory;
  private final Waiter waiter;
  private final Duration delay;

  private final Executor executor;

  public void solve(Attempt attempt) {
    var runnable =
        AttemptSolverRunnable.builder()
            .answerFinder(answerFinder)
            .repository(repository)
            .patternFactory(patternFactory)
            .waiter(waiter)
            .delay(delay)
            .attemptId(attempt.id())
            .maxPasses(40)
            .build();
    executor.execute(runnable);
  }
}
