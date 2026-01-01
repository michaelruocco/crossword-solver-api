package uk.co.mruoc.cws.usecase;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import uk.co.mruoc.cws.entity.Candidates;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.entity.Clues;
import uk.co.mruoc.cws.entity.Id;

@Builder
@Slf4j
public class CandidateLoader {

  private final AnswerFinder answerFinder;
  private final Executor executor;
  private final CandidateRepository repository;

  public Map<Id, Candidates> loadCandidates(Clues clues) {
    // TODO pass candidates per clue from API when performing automatic solving
    return loadCandidates(clues, 5);
  }

  public Map<Id, Candidates> loadCandidates(Clues clues, int candidatesPerClue) {
    return clues.stream()
        .map(clue -> asyncLoadCandidates(clue, candidatesPerClue))
        .map(CompletableFuture::join)
        .collect(Collectors.toMap(Candidates::id, Function.identity()));
  }

  private CompletableFuture<Candidates> asyncLoadCandidates(Clue clue, int candidatesPerClue) {
    return CompletableFuture.supplyAsync(() -> loadCandidates(clue, candidatesPerClue), executor)
        .orTimeout(30, TimeUnit.SECONDS)
        .exceptionally(error -> handleApiFailure(clue, error));
  }

  private Candidates loadCandidates(Clue clue, int candidatesPerClue) {
    log.debug("getting candidates for clue {}", clue.asString());
    var candidates =
        loadCandidatesFromDatabase(clue)
            .orElseGet(() -> loadCandidatesFromApi(clue, candidatesPerClue));
    log.debug("got {} candidates for {}", candidates.size(), clue.id());
    return candidates;
  }

  private Optional<Candidates> loadCandidatesFromDatabase(Clue clue) {
    return repository.get(clue);
  }

  private Candidates loadCandidatesFromApi(Clue clue, int candidatesPerClue) {
    log.info("loading candidates from api for clue {}", clue.asString());
    var candidates = answerFinder.findCandidates(clue, candidatesPerClue).validAnswers(clue);
    // TODO don't save if no candidates returned at all
    repository.save(candidates);
    return candidates;
  }

  private Candidates handleApiFailure(Clue clue, Throwable error) {
    log.warn("candidate lookup failed for {}", clue.id(), error);
    return new Candidates(clue);
  }
}
