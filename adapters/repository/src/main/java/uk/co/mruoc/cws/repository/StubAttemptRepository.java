package uk.co.mruoc.cws.repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.mruoc.cws.entity.Attempt;
import uk.co.mruoc.cws.usecase.IncrementingIdSupplier;
import uk.co.mruoc.cws.usecase.attempt.AttemptRepository;

@RequiredArgsConstructor
@Slf4j
public class StubAttemptRepository implements AttemptRepository {

  private final Map<Long, Attempt> values;
  private final Supplier<Long> nextId;

  public StubAttemptRepository() {
    this(new ConcurrentHashMap<>(), new IncrementingIdSupplier());
  }

  @Override
  public long getNextId() {
    return nextId.get();
  }

  @Override
  public Optional<Attempt> findById(long id) {
    return Optional.ofNullable(values.get(id));
  }

  @Override
  public void save(Attempt attempt) {
    values.put(attempt.id(), attempt);
  }
}
