package uk.co.mruoc.cws.entity;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@ToString
public class Candidates implements Iterable<Answer> {

  private final Clue clue;
  private final Collection<Answer> values;

  public Candidates(Clue clue, Answer... answers) {
    this(clue, List.of(answers));
  }

  public Id getId() {
    return clue.id();
  }

  public Candidates withId(Id id) {
    return new Candidates(clue.withId(id), values.stream().map(a -> a.withId(id)).toList());
  }

  public Clue clue() {
    return clue;
  }

  @Override
  @NonNull
  public Iterator<Answer> iterator() {
    return values.iterator();
  }

  public Candidates addAll(Candidates otherCandidates) {
    var map = this.stream().collect(Collectors.toMap(Answer::value, Function.identity()));
    var otherMap =
        otherCandidates.stream().collect(Collectors.toMap(Answer::value, Function.identity()));
    map.putAll(otherMap);
    return new Candidates(clue, map.values());
  }

  public Stream<Answer> stream() {
    return values.stream();
  }

  public int getCluePatternCharCount() {
    return clue.patternCharCount();
  }

  public Candidates sortByScore() {
    return new Candidates(
        clue,
        values.stream()
            .sorted(Comparator.comparingInt(Answer::confidenceScore).reversed())
            .toList());
  }

  public int size() {
    return values.size();
  }

  public int getBestScore() {
    return getBest().map(Answer::confidenceScore).orElse(0);
  }

  public Optional<Answer> getBest() {
    return values.stream().max(Comparator.comparingInt(Answer::confidenceScore));
  }

  public Candidates getFirst(int n) {
    return new Candidates(clue, values.stream().limit(n).toList());
  }

  public Candidates getValidAnswers(Clue clue) {
    return new Candidates(clue, values.stream().filter(new ValidAnswerPredicate(clue)).toList());
  }

  public boolean isEmpty() {
    return values.isEmpty();
  }

  public String asString() {
    return String.format(
        "clue %s %s %s -> has %d candidates and best score %d %s",
        clue.id(), clue.text(), clue.pattern(), values.size(), getBestScore(), valuesAsString());
  }

  public String valuesAsString() {
    return values.stream().map(Answer::value).collect(Collectors.joining(", "));
  }
}
