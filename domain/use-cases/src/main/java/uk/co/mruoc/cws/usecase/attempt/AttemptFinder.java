package uk.co.mruoc.cws.usecase.attempt;

import lombok.RequiredArgsConstructor;
import uk.co.mruoc.cws.entity.Attempt;

@RequiredArgsConstructor
public class AttemptFinder {

  private final AttemptRepository repository;

  public Attempt findById(Long id) {
    return repository.findById(id).orElseThrow(() -> new AttemptNotFoundByIdException(id));
  }
}
