package uk.co.mruoc.cws.usecase.puzzle;

import lombok.Builder;
import uk.co.mruoc.cws.entity.Puzzle;
import uk.co.mruoc.cws.usecase.Image;

@Builder
public class PuzzleService {

  private final PuzzleCreator creator;
  private final PuzzleFinder finder;

  public long create(String imageUrl) {
    return creator.create(imageUrl);
  }

  public long create(Image image) {
    return creator.create(image);
  }

  public Puzzle findById(Long id) {
    return finder.findById(id);
  }
}
