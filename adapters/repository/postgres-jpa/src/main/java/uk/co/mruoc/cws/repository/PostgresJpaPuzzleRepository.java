package uk.co.mruoc.cws.repository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.co.mruoc.cws.repository.entity.PuzzleEntity;
import uk.co.mruoc.cws.repository.entity.PuzzleSummaryProjection;

@Repository
public interface PostgresJpaPuzzleRepository extends JpaRepository<PuzzleEntity, UUID> {

  @Query(
      """
        SELECT
            p.id AS id,
            p.name AS name,
            COUNT(a) AS attemptCount
        FROM PuzzleEntity p
        LEFT JOIN AttemptEntity a ON a.puzzle = p
        GROUP BY p.id, p.name
    """)
  Collection<PuzzleSummaryProjection> findAllSummaries();

  Optional<PuzzleEntity> findByHash(String hash);
}
