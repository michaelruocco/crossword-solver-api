package uk.co.mruoc.cws.usecase.puzzle;

import java.util.Collection;
import uk.co.mruoc.cws.entity.PuzzleSummary;

public interface PuzzleSummaryRepository {

  Collection<PuzzleSummary> findAllSummaries();
}
