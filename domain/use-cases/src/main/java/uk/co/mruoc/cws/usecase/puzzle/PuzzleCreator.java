package uk.co.mruoc.cws.usecase.puzzle;

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
    validateDoesNotAlreadyExist(image);
    var puzzle = toPuzzle(image);
    repository.save(puzzle);
    return puzzle.getId();
  }

  private void validateDoesNotAlreadyExist(Image image) {
    var hash = image.getHash();
    repository
        .findByHash(hash)
        .ifPresent(puzzle -> throwPuzzleAlreadyExistsException(puzzle.getId(), hash));
  }

  private void throwPuzzleAlreadyExistsException(long id, String hash) {
    throw new PuzzleImageUrlAlreadyExistsException(id, hash);
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
