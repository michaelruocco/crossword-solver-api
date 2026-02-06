package uk.co.mruoc.cws.usecase.puzzle;

import java.util.Optional;
import java.util.UUID;

import uk.co.mruoc.cws.entity.Puzzle;

public interface PuzzleRepository {

  Optional<Puzzle> findById(UUID id);

  Optional<Puzzle> findByHash(String hash);

  void save(Puzzle puzzle);
}
