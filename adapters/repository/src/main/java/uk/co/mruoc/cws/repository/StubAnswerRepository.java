package uk.co.mruoc.cws.repository;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.mruoc.cws.entity.Answer;
import uk.co.mruoc.cws.entity.AnswerNotFoundForIdException;
import uk.co.mruoc.cws.entity.Id;
import uk.co.mruoc.cws.usecase.AnswerRepository;

@RequiredArgsConstructor
@Slf4j
public class StubAnswerRepository implements AnswerRepository {

  private final Map<Id, Answer> values;

  public StubAnswerRepository() {
    this(new ConcurrentHashMap<>());
  }

  @Override
  public void save(Id id, Answer answer) {
    log.debug("saving answer {} for id {}", answer, id);
    values.put(id, answer);
  }

  @Override
  public Optional<Character> getLetter(Id id, int index) {
    return find(id).filter(Answer::confirmed).flatMap(answer -> letterAt(answer, index));
  }

  @Override
  public Collection<Answer> getUnconfirmedAnswersByConfidenceScoreDescending() {
    return values.values().stream()
        .filter(Predicate.not(Answer::confirmed))
        .sorted(Comparator.comparingInt(Answer::confidenceScore).reversed())
        .toList();
  }

  @Override
  public Collection<Answer> getAnswersByConfidenceScoreDescending() {
    return values.values().stream()
        .sorted(Comparator.comparingInt(Answer::confidenceScore).reversed())
        .toList();
  }

  @Override
  public Answer forceFind(Id id) {
    return find(id).orElseThrow(() -> new AnswerNotFoundForIdException(id));
  }

  @Override
  public Optional<Answer> find(Id id) {
    var answer = Optional.ofNullable(values.get(id));
    log.debug("found answer {} for id {}", answer, id);
    return answer;
  }

  private static Optional<Character> letterAt(Answer answer, int index) {
    var value = answer.value();
    if (index < value.length()) {
      return Optional.of(value.charAt(index));
    }
    return Optional.empty();
  }

  @Override
  public OptionalInt getHighestUnconfirmedConfidenceScore() {
    return values.values().stream()
        .filter(a -> !a.confirmed())
        .sorted(Comparator.comparingInt(Answer::confidenceScore).reversed())
        .mapToInt(Answer::confidenceScore)
        .findFirst();
  }

  @Override
  public void confirmAnswersWithScore(int requiredScore) {
    values.values().stream()
        .filter(a -> a.confidenceScore() == requiredScore)
        .map(Answer::confirm)
        .forEach(confirmedAnswer -> values.put(confirmedAnswer.id(), confirmedAnswer));
  }
}
