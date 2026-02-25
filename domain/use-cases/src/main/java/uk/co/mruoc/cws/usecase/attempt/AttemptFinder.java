package uk.co.mruoc.cws.usecase.attempt;

import java.util.Collection;
import java.util.UUID;
import lombok.Builder;
import uk.co.mruoc.cws.entity.Attempt;
import uk.co.mruoc.cws.entity.AttemptSummary;

@Builder
public class AttemptFinder {

  private final AttemptRepository attemptRepository;
  private final AttemptSummaryRepository summaryRepository;

  public void validateExistsById(UUID id) {
    if (!attemptRepository.existsById(id)) {
      throw new AttemptNotFoundByIdException(id);
    }
  }

  public Attempt findById(UUID id) {
    return forceFindById(id);
  }

  private Attempt forceFindById(UUID id) {
    return attemptRepository.findById(id).orElseThrow(() -> new AttemptNotFoundByIdException(id));
  }

  public Collection<AttemptSummary> findSummariesByPuzzleId(UUID puzzleId) {

    return summaryRepository.findSummariesByPuzzleId(puzzleId);
  }
}
