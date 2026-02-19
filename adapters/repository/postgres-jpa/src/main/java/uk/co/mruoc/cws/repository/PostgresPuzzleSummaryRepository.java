package uk.co.mruoc.cws.repository;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import uk.co.mruoc.cws.entity.PuzzleSummary;
import uk.co.mruoc.cws.usecase.puzzle.PuzzleSummaryRepository;

@RequiredArgsConstructor
public class PostgresPuzzleSummaryRepository implements PuzzleSummaryRepository {

  private final PostgresJpaPuzzleRepository jpaRepository;
  private final PuzzleEntityConverter entityConverter;

  public PostgresPuzzleSummaryRepository(PostgresJpaPuzzleRepository jpaRepository) {
    this(jpaRepository, new PuzzleEntityConverter());
  }

  @Transactional(readOnly = true)
  @Override
  public Collection<PuzzleSummary> findAllSummaries() {
    return entityConverter.toPuzzleSummaries(jpaRepository.findAllSummaries());
  }
}
