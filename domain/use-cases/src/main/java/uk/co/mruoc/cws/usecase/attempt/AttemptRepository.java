package uk.co.mruoc.cws.usecase.attempt;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import uk.co.mruoc.cws.entity.Attempt;

public interface AttemptRepository {

  boolean existsById(UUID id);

  Optional<Attempt> findById(UUID id);

  void save(Attempt attempt);

  long getAttemptCount(UUID puzzleId);

  void deleteAllByPuzzleId(UUID puzzleId);

  Collection<Attempt> findAll();
}
