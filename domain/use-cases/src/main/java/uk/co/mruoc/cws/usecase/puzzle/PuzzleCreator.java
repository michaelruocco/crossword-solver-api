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

    // calculate hash from image bytes and use that
    // to check if puzzle already exists
    // instead of storing url against puzzle we can store name and hash to ensure uniqueness
    // this should fit better with enabling files to be uploaded directly too

    validateDoesNotAlreadyExist(imageUrl);

    // build puzzle using filename and bytes instead of passing download
    var puzzle = toPuzzle(image);
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
