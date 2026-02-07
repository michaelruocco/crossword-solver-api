package uk.co.mruoc.cws.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.mruoc.cws.repository.entity.PuzzleEntity;

@Repository
public interface PostgresJpaPuzzleRepository extends CrudRepository<PuzzleEntity, UUID> {

  Optional<PuzzleEntity> findByHash(String hash);
}
