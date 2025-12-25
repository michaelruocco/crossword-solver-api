package uk.co.mruoc.cws.usecase.attempt;

import lombok.RequiredArgsConstructor;
import uk.co.mruoc.cws.entity.Attempt;

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
    return greedySolver.solve(initial);
  }
}
