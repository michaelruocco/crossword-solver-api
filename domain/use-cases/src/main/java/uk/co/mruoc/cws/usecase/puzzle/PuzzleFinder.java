package uk.co.mruoc.cws.usecase.puzzle;

import java.util.Collection;
import java.util.UUID;
import lombok.Builder;
import uk.co.mruoc.cws.entity.Puzzle;
import uk.co.mruoc.cws.entity.PuzzleSummary;

@Builder
public class PuzzleFinder {

  private final PuzzleSummaryRepository summaryRepository;
  private final PuzzleRepository puzzleRepository;

  public Collection<PuzzleSummary> findAllSummaries() {
    return summaryRepository.findAllSummaries();
  }

  public Puzzle findById(UUID id) {
    return puzzleRepository.findById(id).orElseThrow(() -> new PuzzleNotFoundByIdException(id));
  }
}
