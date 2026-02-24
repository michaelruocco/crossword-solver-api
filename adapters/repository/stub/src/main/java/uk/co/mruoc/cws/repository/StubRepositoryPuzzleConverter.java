package uk.co.mruoc.cws.repository;

import lombok.RequiredArgsConstructor;
import uk.co.mruoc.cws.entity.Puzzle;
import uk.co.mruoc.cws.entity.PuzzleSummary;
import uk.co.mruoc.cws.usecase.attempt.AttemptRepository;

@RequiredArgsConstructor
public class StubRepositoryPuzzleConverter {

  private final AttemptRepository attemptRepository;

  public PuzzleSummary toSummary(Puzzle puzzle) {
    return PuzzleSummary.builder()
        .id(puzzle.getId())
        .name(puzzle.getName())
        .createdAt(puzzle.getCreatedAt())
        .attemptCount(attemptRepository.getAttemptCount(puzzle.getId()))
        .clueCount(puzzle.clueCount())
        .build();
  }
}
