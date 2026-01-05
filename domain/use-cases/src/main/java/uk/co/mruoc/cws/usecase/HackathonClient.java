package uk.co.mruoc.cws.usecase;

import java.util.Collection;
import java.util.Optional;

import uk.co.mruoc.cws.entity.Attempt;
import uk.co.mruoc.cws.entity.Result;

public interface HackathonClient {

  Collection<String> getPuzzleImageUrls();

  Result recordAnswers(Attempt attempt);
}
