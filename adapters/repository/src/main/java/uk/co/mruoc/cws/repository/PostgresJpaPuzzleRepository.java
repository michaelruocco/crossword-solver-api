package uk.co.mruoc.cws.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import uk.co.mruoc.cws.repository.entity.PuzzleEntity;

@Repository
public interface PostgresJpaPuzzleRepository extends CrudRepository<PuzzleEntity, Long> {

  @Query(value = "SELECT nextval('puzzle_id_sequence')", nativeQuery = true)
  Long getNextId();

  Optional<PuzzleEntity> findByHash(String hash);
}
