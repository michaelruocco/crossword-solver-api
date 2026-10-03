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
    return find(id).isPresent();
  }

  public Clue forceFind(Id id) {
    return find(id).orElseThrow(() -> new ClueNotFoundForIdException(id));
  }

  public Clues normalizeTextHyphens() {
    return new Clues(stream().map(Clue::normalizeHyphens).toList());
  }

  public Optional<Clue> find(Id id) {
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

  public Clues update(Clue clue) {
    var updatedClues = copyValues();
    updatedClues.put(clue.id(), clue);
    return new Clues(updatedClues);
  }

  public Clues getDown() {
    return ofDirection(Direction.DOWN);
  }

  public Clues getAcross() {
    return ofDirection(Direction.ACROSS);
  }

  public Clues withType(ClueType type) {
    return new Clues(stream().map(clue -> clue.withType(type)).toList());
  }

  private Map<Id, Clue> copyValues() {
    return new LinkedHashMap<>(values);
  }

  private Clues ofDirection(Direction direction) {
    return new Clues(
        values.values().stream().filter(clue -> clue.direction() == direction).toList());
  }

  private static Map<Id, Clue> toMap(Collection<Clue> clues) {
    return clues.stream()
        .collect(Collectors.toMap(Clue::id, Function.identity(), (_, y) -> y, LinkedHashMap::new));
  }
}
