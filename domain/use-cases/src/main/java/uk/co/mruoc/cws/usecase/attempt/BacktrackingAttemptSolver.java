package uk.co.mruoc.cws.usecase.attempt;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.mruoc.cws.entity.Answer;
import uk.co.mruoc.cws.entity.Attempt;
import uk.co.mruoc.cws.entity.Candidates;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.entity.Clues;
import uk.co.mruoc.cws.entity.Id;
import uk.co.mruoc.cws.usecase.AnswerFinder;
import uk.co.mruoc.cws.usecase.CandidateLoader;
import uk.co.mruoc.cws.usecase.DefaultWaiter;
import uk.co.mruoc.cws.usecase.PatternFactory;

@RequiredArgsConstructor
@Slf4j
public class BacktrackingAttemptSolver {

  private final AnswerFinder answerFinder;
  private final CandidateLoader candidateLoader;
  private final PatternFactory patternFactory;
  private final SolverConfig config;
  private final Waiter waiter;
  private final Queue<Id> parked;

  public BacktrackingAttemptSolver(AnswerFinder answerFinder, CandidateLoader candidateLoader) {
    this(
        answerFinder,
        candidateLoader,
        new PatternFactory(),
        new SolverConfig(),
        new DefaultWaiter(),
        new ArrayDeque<>());
  }

  public Optional<Attempt> solve(Attempt inputAttempt) {
    if (inputAttempt.isComplete()) {
      return Optional.of(inputAttempt);
    }

    var passAttempt = addPatternsToClues(inputAttempt);
    var allCandidates = candidateLoader.loadCandidates(passAttempt.getClues());
    var sortedCandidates = sort(allCandidates).stream().filter(c -> !c.isEmpty()).toList();

    var candidates =
        sortedCandidates.stream()
            .filter(c -> !passAttempt.hasConfirmedAnswer(c.getId()))
            .filter(c -> !passAttempt.hasConfirmedAnswers() || c.clue().patternCharCount() > 0)
            .filter(c -> !parked.contains(c.getId()))
            .findFirst();
    if (candidates.isEmpty()) {
      if (!parked.isEmpty()) {
        candidates = Optional.of(allCandidates.get(parked.poll()));
      }
    }

    if (candidates.isEmpty()) {
      log.info("no more candidates to solve");
      return Optional.of(inputAttempt);
    }

    log.info("selected candidates {}", candidates.get().asString());
    var answers = candidates.get().sortByScore().stream().filter(passAttempt::accepts).toList();

    if (shouldPark(answers)) {
      log.info("parking clue id {}", answers.getFirst().id());
      parked.add(answers.getFirst().id());
      return solve(inputAttempt);
    }

    for (Answer answer : answers) {
      var confirmed = answer.confirm();
      log.info(
          "confirmed answer {} for clue {}",
          confirmed.asString(),
          candidates.get().clue().asString());

      var candidateAttempt = addPatternsToClues(passAttempt.saveAnswer(confirmed));
      var deadEnd = hasDeadEnd(candidateAttempt, allCandidates);
      if (deadEnd) {
        log.info(
            "dead end for answer {} and clue {}",
            confirmed.asString(),
            candidates.get().clue().asString());
      } else {
        Optional<Attempt> solved = solve(candidateAttempt);
        if (solved.isPresent()) {
          return solved;
        }
      }
    }
    log.info("no valid answers found for candidates {}", candidates.get().asString());
    return Optional.of(inputAttempt);
  }

  private boolean shouldPark(List<Answer> answers) {
    var bestScore = answers.getFirst().confidenceScore();
    if (bestScore <= 50) {
      return true;
    }
    // var worstScore = answers.getLast().confidenceScore();
    // var difference = bestScore - worstScore;
    // if (answers.size() > 1 && difference <= 40) {
    //  return true;
    // }
    if (answers.size() > 1 && answers.stream().allMatch(a -> a.confidenceScore() > 80)) {
      return true;
    }
    return answers.size() == 1 && bestScore <= 75;
  }

  private Attempt addPatternsToClues(Attempt attempt) {
    return attempt.withPuzzle(
        attempt.puzzle().withClues(addPatternsToClues(attempt.getClues(), attempt)));
  }

  private Clues addPatternsToClues(Clues clues, Attempt attempt) {
    for (var clue : clues) {
      var pattern = patternFactory.build(clue, attempt);
      clues = clues.update(clue.withPattern(pattern));
    }
    return clues;
  }

  private Collection<Candidates> sort(Map<Id, Candidates> candidates) {
    var sorted = candidates.values().stream().sorted(new CandidateComparator()).toList();
    // sorted.forEach(c -> log.info(c.asString()));
    return sorted;
  }

  private boolean matchesAllIntersections(
      Clue clue, Answer candidateAnswer, Map<Id, Candidates> allCandidates, Attempt attempt) {
    var intersections = attempt.getIntersections(clue.id());
    log.info(
        "found intersections {} for candidate answer {}",
        intersections.stream().map(i -> i.getIntersectingId(candidateAnswer.id())).toList(),
        candidateAnswer.asString());
    for (var intersection : intersections) {
      var intersectingId = intersection.getIntersectingId(clue.id());
      var intersectingCandidates = allCandidates.get(intersectingId);
      if (intersectingCandidates.stream()
          .allMatch(
              intersectingAnswer ->
                  candidateAnswer.conflictsWith(intersectingAnswer, intersection))) {
        return false;
      }
    }
    log.info("found no conflicts for candidate answer {}", candidateAnswer.asString());
    return true;
  }

  private boolean hasDeadEnd(Attempt attempt, Map<Id, Candidates> allCandidates) {
    for (var candidates : allCandidates.values()) {
      boolean unconfirmed = !attempt.hasConfirmedAnswer(candidates.getId());
      var clue = candidates.clue();
      boolean constrained = clue.isConstrainedByAtLeastNChars(3);
      if (unconfirmed && constrained) {
        boolean anyAcceptable =
            candidates.getValidAnswers(clue).stream().anyMatch(attempt::accepts);
        if (!anyAcceptable) {
          log.info("found real dead end at candidates {} from valid answers {}", candidates.asString(), candidates.getValidAnswers(clue).asString());
          return true;
        }
      }
    }
    return false;
  }
}
