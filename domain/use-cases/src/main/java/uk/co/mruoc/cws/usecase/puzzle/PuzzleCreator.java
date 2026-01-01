package uk.co.mruoc.cws.usecase.puzzle;

import java.util.Optional;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import uk.co.mruoc.cws.entity.Puzzle;
import uk.co.mruoc.cws.entity.WordsFactory;
import uk.co.mruoc.cws.usecase.CellExtractor;
import uk.co.mruoc.cws.usecase.ClueExtractor;
import uk.co.mruoc.cws.usecase.Image;
import uk.co.mruoc.cws.usecase.ImageDownloader;
import uk.co.mruoc.cws.usecase.UrlConverter;

@Builder
@Slf4j
public class PuzzleCreator {

  private final UrlConverter urlConverter;
  private final ImageDownloader imageDownloader;
  private final ClueExtractor clueExtractor;
  private final CellExtractor cellExtractor;
  private final PuzzleRepository repository;
  private final WordsFactory wordsFactory;

  public long create(String imageUrl) {
    var image = imageDownloader.downloadImage(imageUrl);
    return findIdIfAlreadyExists(image).orElseGet(() -> create(image));
  }

  private Optional<Long> findIdIfAlreadyExists(Image image) {
    var hash = image.getHash();
    return repository.findByHash(hash).map(Puzzle::getId);
  }

  private long create(Image image) {
    var puzzle = toPuzzle(image);
    repository.save(puzzle);
    return puzzle.getId();
  }

  private Puzzle toPuzzle(Image image) {
    log.info("building puzzle from name {} and hash {}", image.getName(), image.getHash());
    var clues = clueExtractor.extractClues(image);
    var cells = cellExtractor.extractCells(image);
    return Puzzle.builder()
        .id(repository.getNextId())
        .name(image.getName())
        .hash(image.getHash())
        .clues(clues)
        .words(wordsFactory.toWords(clues, cells))
        .build();
  }
}
