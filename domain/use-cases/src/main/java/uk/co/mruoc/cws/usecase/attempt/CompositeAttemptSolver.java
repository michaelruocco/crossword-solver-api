package uk.co.mruoc.cws.usecase.attempt;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import uk.co.mruoc.cws.entity.Attempt;

@Slf4j
@Builder
public class CompositeAttemptSolver implements AttemptSolver {

  private final AttemptRepository repository;
  private final BacktrackingAttemptSolver backtrackingSolver;
  private final GreedyAttemptSolver greedySolver;
  private final int maxPasses;

  @Override
  public Attempt solve(Attempt attempt) {
    var passAttempt = attempt;
    int pass = 0;
    while (!passAttempt.isComplete() && pass < maxPasses) {
      passAttempt = performPass(passAttempt, pass);
      pass += 1;
    }
    return passAttempt;
  }

  public Attempt performPass(Attempt attempt, int pass) {
    var passAttempt = backtrackingSolver.solve(attempt);
    if (passAttempt.isComplete()) {
      return passAttempt;
    }
    repository.save(passAttempt);
    log.info("pass {} backtracking attempt incomplete {}", pass, passAttempt.asString());
    passAttempt = greedySolver.solve(passAttempt);
    if (passAttempt.isComplete()) {
      return passAttempt;
    }
    log.info("pass {} greedy attempt incomplete {}", pass, passAttempt.asString());
    repository.save(passAttempt);
    return passAttempt;
    // TODO add another call that checks greedy confirmed answers against candidates
    // and if two equally viable candidates were used try answer finder and
    // if that matches one candidate use that instead this might help with YIP vs YAP
  }
}
