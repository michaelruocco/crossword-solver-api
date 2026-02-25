package uk.co.mruoc.cws.repository;

import java.util.Collection;
import java.util.Comparator;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import uk.co.mruoc.cws.entity.PuzzleSummary;
import uk.co.mruoc.cws.usecase.puzzle.PuzzleRepository;
import uk.co.mruoc.cws.usecase.puzzle.PuzzleSummaryRepository;

@Builder
@Slf4j
public class StubPuzzleSummaryRepository implements PuzzleSummaryRepository {

  private final PuzzleRepository puzzleRepository;
  private final StubRepositoryPuzzleConverter puzzleConverter;

  @Override
  public Collection<PuzzleSummary> findAllSummaries() {
    return puzzleRepository.findAll().stream()
        .map(puzzleConverter::toSummary)
        .sorted(Comparator.comparing(PuzzleSummary::getCreatedAt).reversed())
        .toList();
  }
}
