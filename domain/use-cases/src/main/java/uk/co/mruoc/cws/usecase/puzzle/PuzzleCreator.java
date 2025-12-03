package uk.co.mruoc.cws.usecase.puzzle;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import uk.co.mruoc.cws.entity.Puzzle;
import uk.co.mruoc.cws.entity.WordsFactory;
import uk.co.mruoc.cws.usecase.CellExtractor;
import uk.co.mruoc.cws.usecase.ClueExtractor;
import uk.co.mruoc.cws.usecase.ImageCompressor;
import uk.co.mruoc.cws.usecase.ImageDownloader;

@Builder
@Slf4j
public class PuzzleCreator {

  private final ImageDownloader imageDownloader;
  private final ImageCompressor imageCompressor;
  private final ClueExtractor clueExtractor;
  private final CellExtractor cellExtractor;
  private final PuzzleRepository repository;
  private final WordsFactory wordsFactory;

  public long create(String imageUrl) {
    validateDoesNotAlreadyExist(imageUrl);
    var puzzle = toPuzzle(imageUrl);
    repository.save(puzzle);
    return puzzle.getId();
  }

  private void validateDoesNotAlreadyExist(String imageUrl) {
    repository
        .findByImageUrl(imageUrl)
        .ifPresent(puzzle -> throwPuzzleAlreadyExistsException(puzzle.getId(), imageUrl));
  }

  private void throwPuzzleAlreadyExistsException(long id, String imageUrl) {
    throw new PuzzleImageUrlAlreadyExistsException(id, imageUrl);
  }

  private Puzzle toPuzzle(String imageUrl) {
    log.info("building puzzle from url {}", imageUrl);
    var clues = clueExtractor.extractClues(imageUrl);
    var cells = cellExtractor.extractCells(imageUrl);
    return Puzzle.builder()
        .id(repository.getNextId())
        .imageUrl(imageUrl)
        .clues(clues)
        .words(wordsFactory.toWords(clues, cells))
        .build();
  }
}
