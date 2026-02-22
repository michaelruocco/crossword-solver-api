package uk.co.mruoc.cws.repository;

import java.util.Collection;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import uk.co.mruoc.cws.entity.Puzzle;
import uk.co.mruoc.cws.entity.PuzzleSummary;
import uk.co.mruoc.cws.usecase.attempt.AttemptRepository;
import uk.co.mruoc.cws.usecase.puzzle.PuzzleRepository;
import uk.co.mruoc.cws.usecase.puzzle.PuzzleSummaryRepository;

@Builder
@Slf4j
public class StubPuzzleSummaryRepository implements PuzzleSummaryRepository {

  private final PuzzleRepository puzzleRepository;
  private final AttemptRepository attemptRepository;

  @Override
  public Collection<PuzzleSummary> findAllSummaries() {
    return puzzleRepository.findAll().stream().map(this::toSummary).toList();
  }

  private PuzzleSummary toSummary(Puzzle puzzle) {
    return PuzzleSummary.builder()
        .id(puzzle.getId())
        .name(puzzle.getName())
        .attemptCount(attemptRepository.getAttemptCount(puzzle.getId()))
        .build();
  }
}
