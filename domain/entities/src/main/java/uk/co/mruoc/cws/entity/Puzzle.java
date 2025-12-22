package uk.co.mruoc.cws.entity;

import java.util.Collection;
import lombok.Builder;
import lombok.Data;
import lombok.With;

@Builder
@Data
public class Puzzle {
  private final Long id;
  private final String name;
  private final String hash;

  @With private final Clues clues;

  private final Words words;

  public boolean hasClue(Id id) {
    return clues.hasClue(id);
  }

  public Clue getClue(Id id) {
    return clues.forceFind(id);
  }

  public Collection<Id> getIntersectingIds(Id id) {
    return words.getIntersectingIds(id);
  }

  public Collection<Intersection> getIntersections(Id id) {
    return words.getIntersections(id);
  }

  public Collection<Intersection> getIntersections() {
    return words.getIntersections();
  }

  public Puzzle updateClue(Clue clue) {
    return withClues(clues.update(clue));
  }
}
