package uk.co.mruoc.cws.repository;

import java.util.Optional;
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
  public long getNextId() {
    return jpaRepository.getNextId();
  }

  @Transactional(readOnly = true)
  @Override
  public Optional<Attempt> findById(long id) {
    return jpaRepository.findById(id).map(entityConverter::toAttempt);
  }

  @Override
  public void save(Attempt attempt) {
    jpaRepository.save(entityConverter.toEntity(attempt));
  }
}
