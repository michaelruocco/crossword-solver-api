package uk.co.mruoc.cws.usecase;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import uk.co.mruoc.cws.entity.Answer;
import uk.co.mruoc.cws.entity.Answers;
import uk.co.mruoc.cws.entity.Candidates;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.entity.Clues;

@RequiredArgsConstructor
public class CachingAnswerFinder implements AnswerFinder {

  private final AnswerFinder finder;
  private final Map<String, Candidates> candidateCache;
  private final Map<String, Answers> answersCache;
  private final Map<String, Answer> answerCache;

  public CachingAnswerFinder(AnswerFinder finder) {
    this(finder, new HashMap<>(), new HashMap<>(), new HashMap<>());
  }

  @Override
  public Candidates findCandidates(Clue clue, int numberOfCandidates) {
    var key = toKey(clue, numberOfCandidates);
    var candidates =
        Optional.ofNullable(candidateCache.get(key))
            .orElseGet(() -> finder.findCandidates(clue, numberOfCandidates));
    candidateCache.put(key, candidates);
    return candidates;
  }

  @Override
  public Answers findAnswers(Clues clues) {
    var key = toKey(clues);
    var answers =
        Optional.ofNullable(answersCache.get(key)).orElseGet(() -> finder.findAnswers(clues));
    answersCache.put(key, answers);
    return answers;
  }

  @Override
  public Answer findAnswer(Clue clue) {
    var key = toKey(clue);
    var answer = Optional.ofNullable(answerCache.get(key)).orElseGet(() -> finder.findAnswer(clue));
    answerCache.put(key, answer);
    return answer;
  }

  public String toKey(Clues clues) {
    return clues.stream().map(this::toKey).collect(Collectors.joining("|"));
  }

  private String toKey(Clue clue, int numberOfCandidates) {
    return String.format("%s-%d", toKey(clue), numberOfCandidates);
  }

  private String toKey(Clue clue) {
    return String.format("%s-%s-%s", clue.id(), clue.text(), clue.pattern());
  }
}
