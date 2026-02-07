package uk.co.mruoc.cws.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.mruoc.cws.repository.entity.CandidateClueEntity;

@Repository
public interface PostgresJpaCandidateRepository
    extends CrudRepository<CandidateClueEntity, String> {
  // intentionally blank
}
