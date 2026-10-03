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
    var passes = 0;
    var previousConfirmedAnswers = -1L;
    while (shouldContinueSolving(passAttempt, passes, previousConfirmedAnswers)) {
      previousConfirmedAnswers = passAttempt.getConfirmedAnswerCount();
      passAttempt = pass(passAttempt);
      passes++;
    }
    return passAttempt;
  }

  private boolean shouldContinueSolving(
      Attempt passAttempt, int passes, long previousConfirmedAnswerCount) {
    if (passAttempt.allCluesAnswered()) {
      log.info("attempt complete, all clues answered");
      return false;
    }
    if (passes >= 5) {
      log.info("{} surpasses max passes of 5", passAttempt);
      return false;
    }
    var moreCluesConfirmed = previousConfirmedAnswerCount < passAttempt.getConfirmedAnswerCount();
    log.info(
        "previous pass confirmed answers {} current pass confirmed answers {} more clues confirmed {}",
        previousConfirmedAnswerCount,
        passAttempt.getConfirmedAnswerCount(),
        moreCluesConfirmed);
    return moreCluesConfirmed;
  }

  private Attempt pass(Attempt attempt) {
    var passAttempt = patternFactory.addPatternsToClues(attempt);
    var clues = passAttempt.getCluesWithUnconfirmedAnswer();
    var candidates = sort(candidateLoader.loadCandidates(clues));
    for (var clueCandidates : candidates) {
      var bestAnswer = clueCandidates.getBestAnswerIfOverallScoreGreaterThan(0.2);
      if (candidates.size() == 1 && bestAnswer.isEmpty()) {
        bestAnswer = clueCandidates.best();
      }
      if (bestAnswer.isPresent() && passAttempt.accepts(bestAnswer.get())) {
        var confirmed = bestAnswer.get().confirm();
        log.info(
            "confirmed answer {} from candidates score {} for candidates {}",
            confirmed,
            clueCandidates.overallScore(),
            clueCandidates.asString());
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
