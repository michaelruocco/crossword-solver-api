package uk.co.mruoc.cws.repository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import uk.co.mruoc.cws.entity.Attempt;
import uk.co.mruoc.cws.usecase.attempt.AttemptRepository;

@RequiredArgsConstructor
public class PostgresAttemptRepository implements AttemptRepository {

  private final PostgresJpaAttemptRepository jpaRepository;
  private final AttemptEntityConverter entityConverter;

  public PostgresAttemptRepository(PostgresJpaAttemptRepository jpaRepository) {
    this(jpaRepository, new AttemptEntityConverter());
  }

  @Override
  public boolean existsById(UUID id) {
    return jpaRepository.existsById(id);
  }

  @Transactional(readOnly = true)
  @Override
  public Optional<Attempt> findById(UUID id) {
    return jpaRepository.findById(id).map(entityConverter::toAttempt);
  }

  @Override
  public void save(Attempt attempt) {
    jpaRepository.save(entityConverter.toEntity(attempt));
  }

  @Override
  public long getAttemptCount(UUID puzzleId) {
    return jpaRepository.countByPuzzleId(puzzleId);
  }

  @Override
  public void deleteAllByPuzzleId(UUID puzzleId) {
    jpaRepository.deleteByPuzzle_Id(puzzleId);
  }

  @Override
  public Collection<Attempt> findAll() {
    return jpaRepository.findAll().stream().map(entityConverter::toAttempt).toList();
  }
}
