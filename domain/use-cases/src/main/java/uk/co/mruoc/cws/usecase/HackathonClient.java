package uk.co.mruoc.cws.usecase;

import java.util.Collection;
import uk.co.mruoc.cws.entity.Attempt;

public interface HackathonClient {

  Collection<String> getPuzzleImageUrls();

  void recordAnswers(Attempt attempt);
}
