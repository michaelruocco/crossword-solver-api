package uk.co.mruoc.cws.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.co.mruoc.cws.repository.entity.AttemptEntity;

@Repository
public interface PostgresJpaAttemptRepository extends CrudRepository<AttemptEntity, UUID> {

  long countByPuzzleId(UUID puzzleId);

  @Modifying
  @Transactional
  void deleteByPuzzle_Id(UUID puzzleId);
}
