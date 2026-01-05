package uk.co.mruoc.cws.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.mruoc.cws.repository.entity.AttemptEntity;

@Repository
public interface PostgresJpaAttemptRepository extends CrudRepository<AttemptEntity, Long> {

  @Query(value = "SELECT nextval('attempt_id_sequence')", nativeQuery = true)
  Long getNextId();
}
