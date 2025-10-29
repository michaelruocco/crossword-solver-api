package uk.co.mruoc.cws.entity;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
public class Clues implements Iterable<Clue> {

  private final Map<Id, Clue> values;

  public Clues(Clue... clues) {
    this(List.of(clues));
  }

  public Clues(Collection<Clue> values) {
    this(toMap(values));
  }

  @Override
  @NonNull
  public Iterator<Clue> iterator() {
    return values.values().iterator();
  }

  public boolean hasClue(Id id) {
    return findClue(id).isPresent();
  }

  public Optional<Clue> findClue(Id id) {
    log.debug("finding text for id {}", id);
    var clue = Optional.ofNullable(values.get(id));
    log.debug("found text {} for id {}", clue, id);
    return clue;
  }

  public Stream<Clue> stream() {
    return values.values().stream();
  }

  public int size() {
    return values.size();
  }

  public Clues addPattern(Id id, String pattern) {
    var clue = findClue(id).orElseThrow();
    var updatedClues = copyValues();
    updatedClues.put(clue.id(), clue.withPattern(pattern));
    return new Clues(updatedClues);
  }

  public Clues getWithLongestPatternIfPossible() {
    var highestPatternCharCount =
        values.values().stream().mapToInt(Clue::getPatternCharCount).max();
    if (highestPatternCharCount.isEmpty()) {
      return this;
    }
    return new Clues(
        values.values().stream()
            .filter(clue -> clue.getPatternCharCount() == highestPatternCharCount.getAsInt())
            .toList());
  }

  public Clues sortByIds(Collection<Id> ids) {
    return new Clues(ids.stream().map(values::get).toList());
  }

  private Map<Id, Clue> copyValues() {
    return new LinkedHashMap<>(values);
  }

  private static Map<Id, Clue> toMap(Collection<Clue> clues) {
    return clues.stream()
        .collect(Collectors.toMap(Clue::id, Function.identity(), (_, y) -> y, LinkedHashMap::new));
  }
}
