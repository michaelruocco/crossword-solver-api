package uk.co.mruoc.cws.entity;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
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

  private final Collection<Answer> values;

  public Candidates(Answer... answers) {
    this(List.of(answers));
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
    return new Candidates(map.values());
  }

  public Stream<Answer> stream() {
    return values.stream();
  }

  public Candidates sortByScore() {
    return new Candidates(
        values.stream()
            .sorted(Comparator.comparingInt(Answer::confidenceScore).reversed())
            .toList());
  }

  public Candidates getFirst(int n) {
    return new Candidates(values.stream().limit(n).toList());
  }

  public Candidates getValidAnswers(Clue clue) {
    return new Candidates(values.stream().filter(new ValidAnswerPredicate(clue)).toList());
  }

  public Candidates withScoreGreaterThanOrEqualTo(int minimumScore) {
    return new Candidates(
        values.stream().filter(clue -> clue.confidenceScore() >= minimumScore).toList());
  }
}
