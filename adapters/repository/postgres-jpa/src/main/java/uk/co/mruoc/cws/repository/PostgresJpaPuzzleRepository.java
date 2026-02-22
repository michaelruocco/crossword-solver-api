package uk.co.mruoc.cws.repository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.co.mruoc.cws.repository.entity.PuzzleEntity;
import uk.co.mruoc.cws.repository.entity.PuzzleSummaryProjection;

@Repository
public interface PostgresJpaPuzzleRepository extends JpaRepository<PuzzleEntity, UUID> {

  Collection<PuzzleSummaryProjection> findAllByOrderByCreatedAtDesc();

  Optional<PuzzleEntity> findByHash(String hash);
}
