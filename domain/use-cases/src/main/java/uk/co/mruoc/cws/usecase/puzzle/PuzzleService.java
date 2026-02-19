package uk.co.mruoc.cws.usecase.puzzle;

import java.util.Collection;
import java.util.UUID;
import lombok.Builder;
import uk.co.mruoc.cws.entity.Puzzle;
import uk.co.mruoc.cws.entity.PuzzleSummary;
import uk.co.mruoc.cws.usecase.Image;

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

  public Collection<PuzzleSummary> findAllSummaries() {
    return finder.findAllSummaries();
  }

  public Puzzle findById(UUID id) {
    return finder.findById(id);
  }
}
