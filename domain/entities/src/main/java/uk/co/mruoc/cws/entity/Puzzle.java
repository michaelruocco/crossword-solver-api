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
  private final String format;
  private final String hash;
  @With private final Clues clues;
  private final Words words;
  private final Grid grid;

  public boolean hasClue(Id id) {
    return clues.hasClue(id);
  }

  public Clue clue(Id id) {
    return clues.forceFind(id);
  }

  public Collection<Id> intersectingIds(Id id) {
    return words.getIntersectingIds(id);
  }

  public Collection<Intersection> intersections(Id id) {
    return words.getIntersections(id);
  }

  public int numberOfClues() {
    return clues.size();
  }

  public Puzzle withClue(Clue clue) {
    return withClues(clues.update(clue));
  }

  public Cells cells() {
    return grid.cells();
  }
}
