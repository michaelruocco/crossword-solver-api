package uk.co.mruoc.cws.usecase.attempt;

import java.util.Optional;
import uk.co.mruoc.cws.entity.Attempt;

public interface AttemptRepository {

  long getNextId();

  default Attempt forceFindById(long id) {
    return findById(id).orElseThrow(() -> new AttemptNotFoundByIdException(id));
  }

  Optional<Attempt> findById(long id);

  void save(Attempt attempt);
}
