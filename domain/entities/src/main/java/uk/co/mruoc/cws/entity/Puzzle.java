package uk.co.mruoc.cws.entity;

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

  public Optional<Clue> getClue(Id id) {
    return clues.findClue(id);
  }
}
