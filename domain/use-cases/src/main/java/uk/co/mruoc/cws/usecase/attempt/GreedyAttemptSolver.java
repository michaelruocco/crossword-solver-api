package uk.co.mruoc.cws.usecase.attempt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.mruoc.cws.entity.Answer;
import uk.co.mruoc.cws.entity.Answers;
import uk.co.mruoc.cws.entity.Attempt;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.entity.Clues;
import uk.co.mruoc.cws.entity.Id;
import uk.co.mruoc.cws.entity.ValidAnswerPredicate;
import uk.co.mruoc.cws.usecase.AnswerFinder;
import uk.co.mruoc.cws.usecase.ClueRanker;
import uk.co.mruoc.cws.usecase.PatternFactory;

@Builder
@RequiredArgsConstructor
@Slf4j
public class GreedyAttemptSolver implements AttemptSolver {

  private final AnswerFinder answerFinder;
  private final ClueRanker clueRanker;
  private final PatternFactory patternFactory;
  private final int maxPassesBuffer;
  private final int maxCluesOnPass;

  public GreedyAttemptSolver(AnswerFinder answerFinder, ClueRanker clueRanker) {
    // TODO configure max attempts buffer and max clues on pass externally
    this(answerFinder, clueRanker, new PatternFactory(), 10, 10);
  }

  @Override
  public Attempt solve(Attempt attempt) {
    int pass = 1;
    var maxPasses = attempt.getNumberOfClues() + maxPassesBuffer;
    while (!attempt.isComplete() && pass <= maxPasses) {
      attempt = performPass(attempt, pass);
      pass++;
    }
    log.info(
        "attempt {} completed in {} of max allowed {} passes from {} clues with buffer {}",
        attempt.id(),
        pass,
        maxPasses,
        attempt.getNumberOfClues(),
        maxPassesBuffer);
    return attempt;
  }

  public Attempt performPass(Attempt attempt, int pass) {
    var updatedAttempt = tryAnswers(attempt);
    log.info("pass {} updated attempt {}", pass, updatedAttempt.id());
    return patternFactory.addPatternsToClues(updatedAttempt).removeInconsistentAnswers();
  }

  private Attempt tryAnswers(Attempt attempt) {
    var selectedClues = selectClues(attempt);
    log.info("selected {} clues", selectedClues.size());
    var answers = getAnswers(selectedClues).confirmAll();
    if (answers.isEmpty()) {
      log.info("unconfirming answers intersecting with clues {}", selectedClues.ids());
      return unconfirmIntersectingClues(attempt, selectedClues);
    }
    log.debug("got {} valid best scoring answers", answers.size());
    logAnswers(answers, selectedClues);
    return attempt.saveAnswers(answers);
  }

  private Attempt unconfirmIntersectingClues(Attempt attempt, Clues clues) {
    var candidateAttempt = attempt;
    for (var clue : clues) {
      candidateAttempt = unconfirmIntersectingClue(candidateAttempt, clue);
    }
    throw new SolverException(String.format("no clues to retry for attempt %s", attempt.id()));
  }

  private Attempt unconfirmIntersectingClue(Attempt attempt, Clue clue) {
    var intersectingIds = new ArrayList<>(attempt.getIntersectingIds(clue.id()));
    log.info("found intersecting ids {} for {}", intersectingIds, clue.id());
    return switch (intersectingIds.size()) {
      case 0 -> throw new NoIntersectingIdsToRetryException(attempt, clue);
      case 1 -> unconfirm(attempt, clue, intersectingIds.getFirst());
      default -> unconfirm(attempt, clue, intersectingIds);
    };
  }

  private Attempt unconfirm(Attempt attempt, Clue clue, Id intersectingId) {
    var updatedPattern = patternFactory.build(clue, attempt.unconfirmAnswer(intersectingId));
    var updatedClue = clue.withPattern(updatedPattern);
    return getAnswer(updatedClue)
        .map(updatedAnswer -> attempt.saveAnswer(updatedAnswer.confirm()))
        .orElse(attempt);
  }

  private Attempt unconfirm(Attempt attempt, Clue clue, Collection<Id> intersectingIds) {
    var candidateAttempt = attempt;
    for (var intersectingId : intersectingIds) {
      candidateAttempt = candidateAttempt.unconfirmAnswer(intersectingId);
      var updatedClue = patternFactory.addPatternToClue(clue, candidateAttempt);
      var updatedAnswer = getAnswer(updatedClue);
      if (updatedAnswer.isPresent()) {
        logAnswer(updatedAnswer.get(), updatedClue);
        candidateAttempt = candidateAttempt.saveAnswer(updatedAnswer.get().confirm());
        var intersectingClue = candidateAttempt.getClue(intersectingId);
        var updatedIntersectingClue =
            patternFactory.addPatternToClue(intersectingClue, candidateAttempt);
        var updatedIntersectingAnswer = getAnswer(updatedIntersectingClue);
        if (updatedIntersectingAnswer.isPresent()) {
          logAnswer(updatedIntersectingAnswer.get(), updatedIntersectingClue);
          return candidateAttempt.saveAnswer(updatedIntersectingAnswer.get().confirm());
        }
      }
    }
    return candidateAttempt.removeUnconfirmedAnswers();
  }

  private Answers getAnswers(Clues selectedClues) {
    return answerFinder.findAnswers(selectedClues).validAnswers(selectedClues).sortByScore().top(1);
  }

  private Optional<Answer> getAnswer(Clue clue) {
    var answer = answerFinder.findAnswer(clue);
    if (new ValidAnswerPredicate(clue).test(answer)) {
      return Optional.of(answer);
    }
    return Optional.empty();
  }

  private void logAnswers(Answers answers, Clues clues) {
    answers.forEach(answer -> logAnswer(answer, clues.find(answer.id()).orElseThrow()));
  }

  private void logAnswer(Answer answer, Clue clue) {
    log.info(
        "answer {} confirmed {} with score {} for text {} {} with pattern {}",
        answer.value(),
        answer.confirmed(),
        answer.confidenceScore(),
        answer.id(),
        clue.text(),
        clue.pattern());
  }

  private Clues selectClues(Attempt attempt) {
    var unconfirmedClues = attempt.getCluesWithUnconfirmedAnswer();
    var selectedClues =
        patternFactory.addPatternsToClues(unconfirmedClues, attempt).withLongestPattern();
    if (attempt.getClues().size() == selectedClues.size()) {
      log.info("all clues selected, attempting to select {} easiest", maxCluesOnPass);
      var rankedClues = clueRanker.rankByEase(selectedClues);
      return rankedClues.first(maxCluesOnPass);
    }
    return selectedClues;
  }
}
