package uk.co.mruoc.cws.usecase.puzzle;

import java.util.Optional;
import uk.co.mruoc.cws.entity.Puzzle;

public interface PuzzleRepository {

  long getNextId();

  Optional<Puzzle> findById(long id);

  Optional<Puzzle> findByHash(String hash);

  void save(Puzzle puzzle);
}
