package uk.co.mruoc.cws.repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.mruoc.cws.entity.Attempt;
import uk.co.mruoc.cws.usecase.attempt.AttemptRepository;

@RequiredArgsConstructor
@Slf4j
public class StubAttemptRepository implements AttemptRepository {

  private final Map<UUID, Attempt> values;

  public StubAttemptRepository() {
    this(new ConcurrentHashMap<>());
  }

  @Override
  public Optional<Attempt> findById(UUID id) {
    return Optional.ofNullable(values.get(id));
  }

  @Override
  public void save(Attempt attempt) {
    values.put(attempt.id(), attempt);
  }
}
