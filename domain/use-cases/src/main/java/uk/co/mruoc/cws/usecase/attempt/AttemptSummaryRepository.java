package uk.co.mruoc.cws.usecase.attempt;

import java.util.Collection;
import java.util.UUID;
import uk.co.mruoc.cws.entity.AttemptSummary;

public interface AttemptSummaryRepository {

  Collection<AttemptSummary> findSummariesByPuzzleId(UUID puzzleId);
}
