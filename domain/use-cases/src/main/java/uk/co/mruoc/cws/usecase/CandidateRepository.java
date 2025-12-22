package uk.co.mruoc.cws.usecase;

import java.util.Optional;
import uk.co.mruoc.cws.entity.Candidates;
import uk.co.mruoc.cws.entity.Clue;

public interface CandidateRepository {

  void save(Candidates candidates);

  Optional<Candidates> get(Clue clue);
}
