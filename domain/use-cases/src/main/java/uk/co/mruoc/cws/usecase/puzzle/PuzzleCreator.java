package uk.co.mruoc.cws.usecase.puzzle;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import uk.co.mruoc.cws.entity.Puzzle;
import uk.co.mruoc.cws.entity.WordsFactory;
import uk.co.mruoc.cws.usecase.CluesFactory;
import uk.co.mruoc.cws.usecase.GridExtractor;
import uk.co.mruoc.cws.usecase.Image;
import uk.co.mruoc.cws.usecase.ImageDownloader;

@Builder
@Slf4j
public class PuzzleCreator {

  private final ImageDownloader imageDownloader;
  private final ImageValidator validator;
  private final Supplier<UUID> idSupplier;
  private final CluesFactory cluesFactory;
  private final GridExtractor gridExtractor;
  private final PuzzleRepository repository;
  private final WordsFactory wordsFactory;

  public UUID create(String imageUrl) {
    var image = imageDownloader.downloadImage(imageUrl);
    return create(image);
  }

  public UUID create(Image image) {
    return findIdIfAlreadyExists(image).orElseGet(() -> doCreate(image));
  }

  private Optional<UUID> findIdIfAlreadyExists(Image image) {
    var hash = image.getHash();
    return repository.findByHash(hash).map(Puzzle::getId);
  }

  private UUID doCreate(Image image) {
    validator.validate(image);
    var puzzle = toPuzzle(image);
    repository.save(puzzle);
    return puzzle.getId();
  }

  private Puzzle toPuzzle(Image image) {
    log.info(
        "building puzzle from name {} and hash {} from image with size {}mb",
        image.getName(),
        image.getHash(),
        image.getSizeInMB());
    var clues = cluesFactory.build(image);
    var grid = gridExtractor.extractGrid(image);
    return Puzzle.builder()
        .id(idSupplier.get())
        .name(image.getName())
        .format(image.getFormat())
        .hash(image.getHash())
        .clues(clues)
        .grid(grid)
        .words(wordsFactory.toWords(clues, grid.cells()))
        .build();
  }
}
