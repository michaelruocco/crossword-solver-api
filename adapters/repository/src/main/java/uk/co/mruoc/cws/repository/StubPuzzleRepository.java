package uk.co.mruoc.cws.repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.mruoc.cws.entity.Puzzle;
import uk.co.mruoc.cws.usecase.IncrementingIdSupplier;
import uk.co.mruoc.cws.usecase.puzzle.PuzzleRepository;

@RequiredArgsConstructor
@Slf4j
public class StubPuzzleRepository implements PuzzleRepository {

  private final Map<Long, Puzzle> values;
  private final Supplier<Long> nextId;

  public StubPuzzleRepository() {
    this(new ConcurrentHashMap<>(), new IncrementingIdSupplier());
  }

  @Override
  public long getNextId() {
    return nextId.get();
  }

  @Override
  public Optional<Puzzle> findById(long id) {
    return Optional.ofNullable(values.get(id));
  }

  @Override
  public Optional<Puzzle> findByImageUrl(String imageUrl) {
    return values.values().stream().filter(p -> p.getImageUrl().equals(imageUrl)).findFirst();
  }

  @Override
  public void save(Puzzle puzzle) {
    values.put(puzzle.getId(), puzzle);
  }
}
