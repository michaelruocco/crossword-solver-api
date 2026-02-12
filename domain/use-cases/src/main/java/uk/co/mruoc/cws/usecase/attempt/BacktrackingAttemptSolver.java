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
import uk.co.mruoc.cws.entity.Id;
import uk.co.mruoc.cws.usecase.CandidateLoader;
import uk.co.mruoc.cws.usecase.PatternFactory;

@RequiredArgsConstructor
@Slf4j
public class BacktrackingAttemptSolver implements AttemptSolver {

  private final CandidateLoader candidateLoader;
  private final PatternFactory patternFactory;
  private final Queue<Id> parked;

  public BacktrackingAttemptSolver(CandidateLoader candidateLoader) {
    this(candidateLoader, new PatternFactory(), new ArrayDeque<>());
  }

  @Override
  public Attempt solve(Attempt inputAttempt) {
    if (inputAttempt.isComplete()) {
      return inputAttempt;
    }

    var passAttempt = patternFactory.addPatternsToClues(inputAttempt);
    var remainingCandidates =
        candidateLoader.loadCandidates(passAttempt.getCluesWithUnconfirmedAnswer());
    var sortedCandidates = sort(remainingCandidates).stream().filter(c -> !c.isEmpty()).toList();

    var candidates =
        sortedCandidates.stream()
            .filter(c -> !passAttempt.hasConfirmedAnswers() || c.clue().patternCharCount() > 0)
            .filter(c -> !parked.contains(c.id()))
            .findFirst();
    var wasParked = false;
    if (candidates.isEmpty() && !parked.isEmpty()) {
      candidates = Optional.ofNullable(remainingCandidates.get(parked.poll()));
      wasParked = true;
    }

    if (candidates.isEmpty()) {
      log.info("no more candidates to solve");
      return inputAttempt;
    }

    log.info("selected candidates {}", candidates.get().asString());
    var answers = candidates.get().sortByScore().stream().filter(passAttempt::accepts).toList();

    if (!wasParked && shouldPark(answers)) {
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

      var candidateAttempt = patternFactory.addPatternsToClues(passAttempt.saveAnswer(confirmed));
      var deadEnd = hasDeadEnd(candidateAttempt, remainingCandidates);
      if (deadEnd) {
        log.info(
            "dead end for answer {} and clue {}",
            confirmed.asString(),
            candidates.get().clue().asString());
      } else {
        return solve(candidateAttempt);
      }
    }
    log.info("no valid answers found for candidates {}", candidates.get().asString());
    return inputAttempt;
  }

  private boolean shouldPark(List<Answer> answers) {
    var bestScore = answers.getFirst().confidenceScore();
    if (bestScore <= 50) {
      return true;
    }
    if (answers.size() > 1 && answers.stream().allMatch(a -> a.confidenceScore() > 80)) {
      return true;
    }
    return answers.size() == 1 && bestScore < 65;
  }

  private Collection<Candidates> sort(Map<Id, Candidates> candidates) {
    return candidates.values().stream().sorted(new CandidateComparator()).toList();
  }

  private boolean hasDeadEnd(Attempt attempt, Map<Id, Candidates> remainingCandidates) {
    for (var candidates : remainingCandidates.values()) {
      var clue = candidates.clue();
      boolean constrained = clue.isConstrainedByAtLeastNChars(3);
      if (constrained) {
        boolean anyAcceptable = candidates.validAnswers(clue).stream().anyMatch(attempt::accepts);
        if (!anyAcceptable) {
          log.info(
              "found real dead end at candidates {} from valid answers {}",
              candidates.asString(),
              candidates.validAnswers(clue).asString());
          return true;
        }
      }
    }
    return false;
  }
}
