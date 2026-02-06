package uk.co.mruoc.cws.usecase.attempt;

import java.util.Optional;
import java.util.UUID;
import uk.co.mruoc.cws.entity.Attempt;

public interface AttemptRepository {

  Optional<Attempt> findById(UUID id);

  void save(Attempt attempt);
}
