package uk.co.mruoc.cws.usecase.attempt;

import java.util.Collection;
import java.util.Map;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import uk.co.mruoc.cws.entity.Attempt;
import uk.co.mruoc.cws.entity.Candidates;
import uk.co.mruoc.cws.entity.Id;
import uk.co.mruoc.cws.usecase.CandidateLoader;
import uk.co.mruoc.cws.usecase.PatternFactory;

@Builder
@Slf4j
public class DefaultAttemptSolver implements AttemptSolver {

  private final CandidateLoader candidateLoader;
  private final PatternFactory patternFactory;
  private final AttemptRepository repository;

  @Override
  public Attempt solve(Attempt attempt) {
    var passAttempt = attempt;
    int passes = 0;
    while (!passAttempt.allCluesAnswered() && passes < 5) {
      passAttempt = pass(passAttempt);
      passes++;
    }
    return passAttempt;
  }

  private Attempt pass(Attempt attempt) {
    var passAttempt = patternFactory.addPatternsToClues(attempt);
    var clues = passAttempt.getCluesWithUnconfirmedAnswer();
    var candidates = sort(candidateLoader.loadCandidates(clues));
    for (var clueCandidates : candidates) {
      var bestAnswer = clueCandidates.getBestAnswerIfConfidenceGapGreaterThan(10);
      if (bestAnswer.isPresent() && passAttempt.accepts(bestAnswer.get())) {
        var confirmed = bestAnswer.get().confirm();
        log.info("confirmed answer {}", confirmed);
        passAttempt = passAttempt.saveAnswer(confirmed);
        repository.save(passAttempt);
      }
    }
    return passAttempt;
  }

  private Collection<Candidates> sort(Map<Id, Candidates> candidates) {
    return candidates.values().stream().sorted(new CandidateComparator()).toList();
  }
}
