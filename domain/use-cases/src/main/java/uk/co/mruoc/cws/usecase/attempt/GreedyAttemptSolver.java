package uk.co.mruoc.cws.usecase.attempt;

import java.util.Optional;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.mruoc.cws.entity.Answer;
import uk.co.mruoc.cws.entity.Answers;
import uk.co.mruoc.cws.entity.Attempt;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.entity.Clues;
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
  private final int maxPasses;

  public GreedyAttemptSolver(AnswerFinder answerFinder, ClueRanker clueRanker) {
    this(answerFinder, clueRanker, new PatternFactory(), 60);
  }

  @Override
  public Attempt solve(Attempt attempt) {
    int pass = 1;
    while (!attempt.isComplete() && pass <= maxPasses) {
      attempt = performPass(attempt, pass);
      pass++;
    }
    log.info("attempt {} completed in {} of max allowed {} passes", attempt.id(), pass, maxPasses);
    return attempt;
  }

  public Attempt performPass(Attempt attempt, int pass) {
    var updatedAttempt = tryAnswers(attempt);
    log.info("pass {} updated attempt {}", pass, updatedAttempt.id());
    return updatedAttempt;
  }

  private Attempt tryAnswers(Attempt attempt) {
    var selectedClues = selectClues(attempt);
    log.info("selected {} clues", selectedClues.size());
    var answers = getAnswers(selectedClues).confirmAll();
    if (answers.isEmpty()) {
      log.info("unconfirming answers intersecting with clues {}", selectedClues.ids());
      return retryIntersectingClues(attempt, selectedClues);
    }
    log.debug("got {} valid best scoring answers", answers.size());
    logAnswers(answers, selectedClues);
    return attempt.saveAnswers(answers);
  }

  // TODO split this into its own class and each branch into its own method
  private Attempt retryIntersectingClues(Attempt attempt, Clues clues) {
    for (var clue : clues) {
      var intersectingIds = attempt.getIntersectingIds(clue.id());
      if (intersectingIds.isEmpty()) {
        throw new RuntimeException(
            String.format("no intersecting ids to retry for clue %s", clue.id().toString()));
      } else if (intersectingIds.size() == 1) {
        log.info("one intersecting id found {}", intersectingIds);
        var intersectingId = intersectingIds.stream().findFirst().orElseThrow();
        var updatedClue =
            clue.withPattern(patternFactory.build(clue, attempt.unconfirmAnswer(intersectingId)));
        var updatedAnswer = getAnswer(updatedClue);
        if (updatedAnswer.isPresent()) {
          return attempt.saveAnswer(updatedAnswer.get().confirm());
        }
        return attempt;
      } else {
        var candidateAttempt = attempt;
        for (var intersectingId : intersectingIds) {
          candidateAttempt = candidateAttempt.unconfirmAnswer(intersectingId);
          var updatedClue = clue.withPattern(patternFactory.build(clue, candidateAttempt));
          var updatedAnswer = getAnswer(updatedClue);
          if (updatedAnswer.isPresent()) {
            logAnswer(updatedAnswer.get(), updatedClue);
            candidateAttempt = candidateAttempt.saveAnswer(updatedAnswer.get().confirm());
            var intersectingClue = candidateAttempt.getClue(intersectingId);
            var updatedIntersectingClue =
                intersectingClue.withPattern(
                    patternFactory.build(intersectingClue, candidateAttempt));
            var updatedIntersectingAnswer = getAnswer(updatedIntersectingClue);
            if (updatedIntersectingAnswer.isPresent()) {
              logAnswer(updatedIntersectingAnswer.get(), updatedIntersectingClue);
              return candidateAttempt.saveAnswer(updatedIntersectingAnswer.get().confirm());
            }
          }
        }
        return candidateAttempt;
      }
    }
    throw new RuntimeException("no clues to retry");
  }

  private Answers getAnswers(Clues selectedClues) {
    return answerFinder
        .findAnswers(selectedClues)
        .getValidAnswers(selectedClues)
        .sortByScore()
        .getTop(1);
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
        addPatternsToClues(unconfirmedClues, attempt).getWithLongestPatternIfPossible();
    if (attempt.getClues().size() == selectedClues.size()) {
      log.info("all clues selected, attempting to select 10 easiest");
      var rankedClues = clueRanker.rankByEase(selectedClues);
      return rankedClues.getFirst(10);
    }
    return selectedClues;
  }

  private Clues addPatternsToClues(Clues clues, Attempt attempt) {
    for (var clue : clues) {
      var pattern = patternFactory.build(clue, attempt);
      clues = clues.update(clue.withPattern(pattern));
    }
    return clues;
  }
}
