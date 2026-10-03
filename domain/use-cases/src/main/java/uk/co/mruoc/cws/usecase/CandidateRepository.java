package uk.co.mruoc.cws.usecase;

import java.util.Optional;
import uk.co.mruoc.cws.entity.Candidates;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.entity.ClueCandidateId;

public interface CandidateRepository {

  void save(Candidates candidates);

  Optional<Candidates> get(Clue clue);

  void deleteAll();

  void delete(ClueCandidateId id);
}
