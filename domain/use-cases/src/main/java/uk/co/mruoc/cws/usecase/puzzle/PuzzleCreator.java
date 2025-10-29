package uk.co.mruoc.cws.usecase.puzzle;

import lombok.Builder;
import uk.co.mruoc.cws.entity.Puzzle;
import uk.co.mruoc.cws.usecase.ClueExtractor;
import uk.co.mruoc.cws.usecase.WordExtractor;

@Builder
public class PuzzleCreator {

  private final ClueExtractor clueExtractor;
  private final WordExtractor wordExtractor;
  private final PuzzleRepository repository;

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
    var clues = clueExtractor.extractClues(imageUrl);
    return Puzzle.builder()
        .id(repository.getNextId())
        .imageUrl(imageUrl)
        .clues(clueExtractor.extractClues(imageUrl))
        .words(wordExtractor.extractWords(imageUrl).populateDirectionAndLength(clues))
        .build();
  }
}
