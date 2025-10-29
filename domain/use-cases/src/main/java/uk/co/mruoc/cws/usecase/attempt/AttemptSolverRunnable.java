package uk.co.mruoc.cws.usecase.attempt;

import java.time.Duration;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import uk.co.mruoc.cws.entity.Answer;
import uk.co.mruoc.cws.entity.Answers;
import uk.co.mruoc.cws.entity.Attempt;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.entity.Clues;
import uk.co.mruoc.cws.usecase.AnswerFinder;
import uk.co.mruoc.cws.usecase.PatternFactory;

@Builder
@Slf4j
public class AttemptSolverRunnable implements Runnable {

  private final AnswerFinder answerFinder;
  private final AttemptRepository repository;
  private final PatternFactory patternFactory;
  private final Waiter waiter;
  private final Duration delay;

  private final long attemptId;
  private final int maxPasses;

  @Override
  public void run() {
    int pass = 1;
    var attempt = repository.forceFindById(attemptId);
    while (!attempt.isComplete() && pass <= maxPasses) {
      attempt = performPass(attempt, pass);
      pass++;
    }
  }

  private Attempt performPass(Attempt attempt, int pass) {
    var selectedClues = selectClues(attempt);
    log.info("selected {} clues", selectedClues.size());
    var answers = getAnswers(selectedClues).confirmAll();
    log.info("got {} valid best scoring answers", answers.size());
    logAnswers(answers, selectedClues);
    var updatedAttempt = attempt.saveAnswers(answers);
    repository.save(updatedAttempt);
    log.info("pass {} updated attempt {}", pass, updatedAttempt.id());
    waiter.wait(delay);
    return updatedAttempt;
  }

  private Answers getAnswers(Clues selectedClues) {
    return answerFinder
        .findAnswers(selectedClues)
        .getValidAnswers(selectedClues)
        .sortByScore()
        .getTop(3);
  }

  private void logAnswers(Answers answers, Clues clues) {
    answers.forEach(answer -> logAnswer(answer, clues.findClue(answer.id()).orElseThrow()));
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
    return addPatternsToClues(unconfirmedClues, attempt).getWithLongestPatternIfPossible();
  }

  private Clues addPatternsToClues(Clues clues, Attempt attempt) {
    var confirmedAnswers = attempt.getConfirmedAnswers();
    var words = attempt.getWords();
    for (var confirmedAnswer : confirmedAnswers) {
      var intersectingWords = words.getIntersectingWords(confirmedAnswer.id());
      for (var intersectingWord : intersectingWords) {
        if (!confirmedAnswers.contains(intersectingWord.getId())) {
          var clue = attempt.getClue(intersectingWord.getId()).orElseThrow();
          var pattern =
              patternFactory.build(
                  intersectingWord, words.getIntersections(intersectingWord), confirmedAnswers);
          clues = clues.addPattern(clue.id(), pattern);
        }
      }
    }
    return clues;
  }
}
