package uk.co.mruoc.cws.repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.mruoc.cws.entity.Candidates;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.usecase.CandidateClueHashFactory;
import uk.co.mruoc.cws.usecase.CandidateRepository;

@RequiredArgsConstructor
@Slf4j
public class StubCandidateRepository implements CandidateRepository {

  private final Map<String, Candidates> values;
  private final CandidateClueHashFactory idFactory;

  public StubCandidateRepository() {
    this(new ConcurrentHashMap<>(), new CandidateClueHashFactory());
  }

  @Override
  public void save(Candidates candidates) {
    var clue = candidates.clue();
    var id = idFactory.toId(clue);
    var updated =
        Optional.ofNullable(values.get(id))
            .map(existing -> existing.addAll(candidates))
            .orElse(candidates);
    values.put(id, updated);
  }

  @Override
  public Optional<Candidates> get(Clue clue) {
    var id = idFactory.toId(clue);
    return Optional.ofNullable(values.get(id));
  }
}
