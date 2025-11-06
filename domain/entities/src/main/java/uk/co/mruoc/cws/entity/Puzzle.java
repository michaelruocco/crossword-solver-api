package uk.co.mruoc.cws.entity;

import java.util.Collection;
import java.util.Optional;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Puzzle {
  private final Long id;
  private final String imageUrl;
  private final Clues clues;
  private final Words words;

  public boolean hasClue(Id id) {
    return clues.hasClue(id);
  }

  public Clues getClues(Collection<Id> ids) {
    return new Clues(ids.stream().map(this::getClue).flatMap(Optional::stream).toList());
  }

  public Optional<Clue> getClue(Id id) {
    return clues.findClue(id);
  }

  public Collection<Id> getIntersectingIds(Id id) {
    return words.getIntersectingIds(id);
  }

  public Collection<Intersection> getIntersections(Id id) {
    return words.getIntersections(id);
  }
}
