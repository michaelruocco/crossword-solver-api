package uk.co.mruoc.cws.repository;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import uk.co.mruoc.cws.entity.Candidates;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.usecase.CandidateRepository;

@Slf4j
@RequiredArgsConstructor
public class PostgresCandidateRepository implements CandidateRepository {

  private final PostgresJpaCandidateRepository jpaRepository;
  private final CandidateClueEntityIdFactory idFactory;
  private final CandidateEntityConverter converter;

  public PostgresCandidateRepository(PostgresJpaCandidateRepository jpaRepository) {
    this(jpaRepository, new CandidateClueEntityIdFactory(), new CandidateEntityConverter());
  }

  @Override
  public void save(Candidates candidates) {
    log.info("saving candidates {}", candidates.asString());
    var entity = converter.toEntity(candidates);
    jpaRepository.save(entity);
  }

  @Transactional(readOnly = true)
  @Override
  public Optional<Candidates> get(Clue clue) {
    var id = idFactory.toId(clue);
    var entity = jpaRepository.findById(id);
    return entity.map(converter::toCandidates).map(candidates -> candidates.withClue(clue));
  }
}
