package uk.co.mruoc.cws.repository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.mruoc.cws.entity.Puzzle;
import uk.co.mruoc.cws.usecase.puzzle.PuzzleRepository;

@RequiredArgsConstructor
@Slf4j
public class StubPuzzleRepository implements PuzzleRepository {

  private final Map<UUID, Puzzle> values;

  public StubPuzzleRepository() {
    this(new ConcurrentHashMap<>());
  }

  @Override
  public Collection<Puzzle> findAll() {
    return values.values();
  }

  @Override
  public Optional<Puzzle> findById(UUID id) {
    return Optional.ofNullable(values.get(id));
  }

  @Override
  public Optional<Puzzle> findByHash(String hash) {
    return values.values().stream().filter(p -> p.getHash().equals(hash)).findFirst();
  }

  @Override
  public void save(Puzzle puzzle) {
    values.put(puzzle.getId(), puzzle);
  }
}
