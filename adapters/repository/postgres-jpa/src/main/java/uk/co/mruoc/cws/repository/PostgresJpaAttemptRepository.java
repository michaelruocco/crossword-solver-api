package uk.co.mruoc.cws.repository;

import java.util.Collection;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.co.mruoc.cws.repository.entity.AttemptEntity;
import uk.co.mruoc.cws.repository.entity.AttemptSummaryProjection;

@Repository
public interface PostgresJpaAttemptRepository extends JpaRepository<AttemptEntity, UUID> {

  long countByPuzzleId(UUID puzzleId);

  @Modifying
  @Transactional
  void deleteByPuzzle_Id(UUID puzzleId);

  Collection<AttemptSummaryProjection> findAllByPuzzle_IdOrderByCreatedAtDesc(UUID puzzleId);
}
