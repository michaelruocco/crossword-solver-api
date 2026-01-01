package uk.co.mruoc.cws.usecase;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.mruoc.cws.entity.Answer;
import uk.co.mruoc.cws.entity.Answers;
import uk.co.mruoc.cws.entity.Candidates;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.entity.Clues;

@Slf4j
@RequiredArgsConstructor
public class CompositeAnswerFinder implements AnswerFinder {

  private final Collection<AnswerFinder> finders;

  @Override
  public Candidates findCandidates(Clue clue, int numberOfCandidates) {
    return finders.stream()
        .map(finder -> finder.findCandidates(clue, numberOfCandidates))
        .reduce(Candidates::addAll)
        .map(Candidates::sortByScore)
        .map(c -> c.first(numberOfCandidates))
        .orElse(new Candidates(clue));
  }

  @Override
  public Answer findAnswer(Clue clue) {
    return findAnswers(new Clues(clue)).stream().findFirst().orElse(Answer.noMatch(clue));
  }

  @Override
  public Answers findAnswers(Clues clues) {
    log.info("finding answers for clues");
    clues.forEach(c -> log.info("{} {} {}", c.id(), c.text(), c.pattern()));
    var firstAnswers =
        finders.stream().findFirst().map(finder -> doFind(finder, clues)).orElseThrow();
    if (finders.size() > 1) {
      var otherAnswers = finders.stream().skip(1).map(finder -> doFind(finder, clues)).toList();
      return otherAnswers.stream().reduce(firstAnswers, this::removeDifferent);
    }
    return firstAnswers;
  }

  private Answers doFind(AnswerFinder finder, Clues clues) {
    var answers = finder.findAnswers(clues);
    log.debug("finder {} returned {} answers", finder, answers.size());
    answers.forEach(a -> log.debug("{} {} {}", a.id(), a.value(), a.confidenceScore()));
    return answers;
  }

  private Answers removeDifferent(Answers a1, Answers a2) {
    return a1.removeDifferent(a2);
  }
}
