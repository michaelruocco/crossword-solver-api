package uk.co.mruoc.cws.usecase.attempt;

import lombok.Builder;
import uk.co.mruoc.cws.entity.Answers;
import uk.co.mruoc.cws.entity.Attempt;
import uk.co.mruoc.cws.entity.Puzzle;
import uk.co.mruoc.cws.usecase.UUIDSupplier;
import uk.co.mruoc.cws.usecase.puzzle.PuzzleFinder;

import java.util.UUID;
import java.util.function.Supplier;

@Builder
public class AttemptCreator {

  private final PuzzleFinder puzzleFinder;
  private final AttemptRepository repository;
  private final Supplier<UUID> idSupplier;

  public UUID create(UUID puzzleId) {
    var puzzle = puzzleFinder.findById(puzzleId);
    var attempt = toAttempt(puzzle);
    repository.save(attempt);
    return attempt.id();
  }

  private Attempt toAttempt(Puzzle puzzle) {
    return Attempt.builder()
        .id(idSupplier.get())
        .puzzle(puzzle)
        .answers(new Answers())
        .build();
  }
}
