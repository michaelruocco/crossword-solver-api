package uk.co.mruoc.cws.usecase.attempt;

import java.time.Duration;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.mruoc.cws.entity.Answer;
import uk.co.mruoc.cws.entity.Attempt;
import uk.co.mruoc.cws.entity.Candidates;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.usecase.AnswerFinder;
import uk.co.mruoc.cws.usecase.DefaultWaiter;

@RequiredArgsConstructor
@Slf4j
public class BacktrackingAttemptSolver {

  private final AnswerFinder answerFinder;
  private final SolverConfig config;
  private final ClueSelector clueSelector;
  private final ConstraintPropagator constraintPropagator;
  private final Waiter waiter;

  public BacktrackingAttemptSolver(AnswerFinder answerFinder) {
    this(
        answerFinder,
        new SolverConfig(),
        new ClueSelector(),
        new ConstraintPropagator(),
        new DefaultWaiter());
  }

  public Optional<Attempt> solve(Attempt attempt) {
    log.info("starting backtracking solve for attempt {}", attempt.id());
    return search(attempt, 0);
  }

  private Optional<Attempt> search(Attempt attempt, int depth) {
    if (attempt.isComplete()) {
      log.info("attempt {} complete", attempt.id());
      return Optional.of(attempt);
    }

    if (depth > config.maxDepth()) {
      log.warn("max depth exceeded for attempt {}", attempt.id());
      return Optional.empty();
    }

    if (!attempt.isConsistent()) {
      log.warn("attempt {} is not consistent", attempt.id());
      return Optional.empty();
    }

    Clue clue = clueSelector.selectNextClue(attempt);
    log.info("depth {} selected clue {} {} {}", depth, clue.id(), clue.text(), clue.pattern());

    Candidates candidates =
        answerFinder
            .findCandidates(clue, config.maxCandidatesPerClue())
            .getValidAnswers(clue)
            .withScoreGreaterThanOrEqualTo(60)
            .sortByScore();
    log.info(
        "got candidates {} at depth {} for clue {}",
        candidates.stream()
            .map(a -> String.format("%s %d", a.value(), a.confidenceScore()))
            .collect(Collectors.joining(",")),
        depth,
        clue.id());

    for (Answer candidate : candidates) {
      var confirmedAnswer = candidate.confirm();
      logAnswer(confirmedAnswer, clue, depth);

      Attempt next = attempt.saveAnswer(confirmedAnswer);

      Optional<Attempt> propagated = constraintPropagator.propagate(next, clue.id());
      log.info("propagated answers {}", propagated.map(Attempt::getConfirmedValidAnswers));
      /*log.info(
      "propagated clues {}",
      propagated.map(Attempt::getCluesWithUnconfirmedAnswer).stream()
          .flatMap(c -> c.stream())
          .map(c -> String.format("%s %s %s", c.id(), c.text(), c.pattern()))
          .collect(Collectors.joining(",")));*/
      waiter.wait(Duration.ofSeconds(5));
      if (propagated.isPresent()) {
        Optional<Attempt> solved = search(propagated.get(), depth + 1);
        if (solved.isPresent()) {
          return solved;
        }
      }
    }

    return Optional.empty();
  }

  private void logAnswer(Answer answer, Clue clue, int depth) {
    log.info(
        "answer {} confirmed {} with score {} for text {} {} with pattern {} at depth {}",
        answer.value(),
        answer.confirmed(),
        answer.confidenceScore(),
        answer.id(),
        clue.text(),
        clue.pattern(),
        depth);
  }
}
