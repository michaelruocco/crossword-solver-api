package uk.co.mruoc.cws.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@ToString
public class Answers implements Iterable<Answer> {

  private final Map<Id, Answer> values;

  public Answers(Answer... answers) {
    this(List.of(answers));
  }

  public Answers(Collection<Answer> answers) {
    this(toMap(answers));
  }

  @Override
  @NonNull
  public Iterator<Answer> iterator() {
    return values.values().iterator();
  }

  public boolean isConfirmed(Id id) {
    return findById(id).map(Answer::confirmed).orElse(false);
  }

  public Optional<Answer> findById(Id id) {
    log.debug("finding answer for id {}", id);
    var answer = Optional.ofNullable(values.get(id));
    log.debug("found answer {} for id {}", answer, id);
    return answer;
  }

  public Answers save(Answers otherAnswers) {
    var updatedValues = copyValues();
    updatedValues.putAll(otherAnswers.values);
    return new Answers(updatedValues);
  }

  public Answers save(Answer answer) {
    var updatedValues = copyValues();
    updatedValues.put(answer.id(), answer);
    return new Answers(updatedValues);
  }

  public Answers delete(Id id) {
    var updatedValues = copyValues();
    updatedValues.remove(id);
    return new Answers(updatedValues);
  }

  public Answers getValidAnswers(Clues clues) {
    return filter(new ValidAnswerPredicate(clues));
  }

  public Answers getConfirmedAnswers() {
    return filter(Answer::confirmed);
  }

  public Answers sortByScore() {
    return new Answers(
        values.values().stream()
            .sorted(Comparator.comparingInt(Answer::confidenceScore).reversed())
            .toList());
  }

  public Answers confirmAll() {
    return new Answers(values.values().stream().map(Answer::confirm).toList());
  }

  public Answers getTop(int n) {
    return new Answers(values.values().stream().limit(n).toList());
  }

  public Stream<Answer> stream() {
    return values.values().stream();
  }

  public Optional<Character> getLetterOfConfirmedAnswer(Id id, int index) {
    return findById(id).filter(Answer::confirmed).flatMap(answer -> answer.letterAt(index));
  }

  public int size() {
    return values.size();
  }

  public boolean contains(Id id) {
    return values.containsKey(id);
  }

  public Answers unconfirmAnswer(Id id) {
    var updatedValues = copyValues();
    updatedValues.computeIfPresent(id, (_, answer) -> answer.unconfirm());
    return new Answers(updatedValues);
  }

  public Answers removeUnconfirmed() {
    var unconfirmed = filter(a -> !a.confirmed());
    return removeAll(unconfirmed);
  }

  public Answers removeAll(Answers answersToRemove) {
    var updatedValues = copyValues();
    answersToRemove.stream().map(Answer::id).forEach(updatedValues::remove);
    return new Answers(updatedValues);
  }

  public Answers removeDifferent(Answers newAnswers) {
    var updatedAnswers = new ArrayList<Answer>();
    for (Answer newAnswer : newAnswers) {
      var existing = findById(newAnswer.id());
      if (existing.map(existingAnswer -> existingAnswer.hasSameValue(newAnswer)).orElse(false)) {
        updatedAnswers.add(newAnswer);
      }
    }
    return new Answers(updatedAnswers);
  }

  public boolean isEmpty() {
    return values.isEmpty();
  }

  public boolean areConsistentAt(Intersection intersection) {
    if (!containsAnswers(intersection)) {
      return true;
    }
    var acrossAnswer = findById(intersection.getAcrossId()).orElseThrow();
    var downAnswer = findById(intersection.getDownId()).orElseThrow();
    return !acrossAnswer.conflictsWith(downAnswer, intersection);
  }

  public String asString() {
    return stream()
        .sorted(Comparator.comparing(Answer::idAsString))
        .map(Answer::asString)
        .collect(Collectors.joining(","));
  }

  private boolean containsAnswers(Intersection intersection) {
    return contains(intersection.getAcrossId()) && contains(intersection.getDownId());
  }

  private Answers filter(Predicate<Answer> predicate) {
    return new Answers(values.values().stream().filter(predicate).toList());
  }

  private Map<Id, Answer> copyValues() {
    return new LinkedHashMap<>(values);
  }

  private static Map<Id, Answer> toMap(Collection<Answer> answers) {
    return answers.stream()
        .collect(
            Collectors.toMap(
                Answer::id,
                Function.identity(),
                (_, k2) -> {
                  log.debug("duplicate answer id found {}", k2.id());
                  return k2;
                },
                LinkedHashMap::new));
  }
}
