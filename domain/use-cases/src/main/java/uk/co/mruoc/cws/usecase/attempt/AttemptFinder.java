package uk.co.mruoc.cws.usecase.attempt;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import uk.co.mruoc.cws.entity.Attempt;

@RequiredArgsConstructor
public class AttemptFinder {

  private final AttemptRepository repository;

  public Attempt findById(UUID id) {
    return forceFindById(id);
  }

  private Attempt forceFindById(UUID id) {
    return repository.findById(id).orElseThrow(() -> new AttemptNotFoundByIdException(id));
  }
}
