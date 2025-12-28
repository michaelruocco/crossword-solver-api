package uk.co.mruoc.cws.usecase.attempt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.mruoc.cws.entity.Attempt;

@Slf4j
@RequiredArgsConstructor
public class CompositeAttemptSolver implements AttemptSolver {

  private final BacktrackingAttemptSolver backtrackingSolver;
  private final GreedyAttemptSolver greedySolver;

  @Override
  public Attempt solve(Attempt attempt) {
    var initial = backtrackingSolver.solve(attempt);
    if (initial.isComplete()) {
      return initial;
    }
    log.info("initial backtracking attempt incomplete {}", initial.asString());
    return greedySolver.solve(initial);
  }
}
