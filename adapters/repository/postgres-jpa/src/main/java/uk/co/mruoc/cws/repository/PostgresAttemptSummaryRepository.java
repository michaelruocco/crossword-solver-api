package uk.co.mruoc.cws.repository;

import java.util.Collection;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import uk.co.mruoc.cws.entity.AttemptSummary;
import uk.co.mruoc.cws.usecase.attempt.AttemptSummaryRepository;

@RequiredArgsConstructor
public class PostgresAttemptSummaryRepository implements AttemptSummaryRepository {

  private final PostgresJpaAttemptRepository jpaRepository;
  private final AttemptEntityConverter entityConverter;

  public PostgresAttemptSummaryRepository(PostgresJpaAttemptRepository jpaRepository) {
    this(jpaRepository, new AttemptEntityConverter());
  }

  @Transactional(readOnly = true)
  @Override
  public Collection<AttemptSummary> findSummariesByPuzzleId(UUID puzzleId) {
    return entityConverter.toAttemptSummaries(
        jpaRepository.findAllByPuzzle_IdOrderByCreatedAtDesc(puzzleId));
  }
}
