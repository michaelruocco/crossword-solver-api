package uk.co.mruoc.cws.usecase.attempt;

import lombok.Builder;
import uk.co.mruoc.cws.entity.Answers;
import uk.co.mruoc.cws.entity.Attempt;
import uk.co.mruoc.cws.entity.Puzzle;
import uk.co.mruoc.cws.usecase.puzzle.PuzzleFinder;

@Builder
public class AttemptCreator {

  private final PuzzleFinder puzzleFinder;
  private final AttemptRepository repository;

  public long create(long puzzleId) {
    var puzzle = puzzleFinder.findById(puzzleId);
    var attempt = toAttempt(puzzle);
    repository.save(attempt);
    return attempt.id();
  }

  private Attempt toAttempt(Puzzle puzzle) {
    return Attempt.builder()
        .id(repository.getNextId())
        .puzzle(puzzle)
        .answers(new Answers())
        .build();
  }
}
