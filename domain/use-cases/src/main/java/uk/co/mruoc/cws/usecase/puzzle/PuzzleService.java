package uk.co.mruoc.cws.usecase.puzzle;

import lombok.Builder;
import uk.co.mruoc.cws.entity.Puzzle;
import uk.co.mruoc.cws.usecase.Image;

import java.util.UUID;

@Builder
public class PuzzleService {

  private final PuzzleCreator creator;
  private final PuzzleFinder finder;

  public UUID create(String imageUrl) {
    return creator.create(imageUrl);
  }

  public UUID create(Image image) {
    return creator.create(image);
  }

  public Puzzle findById(UUID id) {
    return finder.findById(id);
  }
}
