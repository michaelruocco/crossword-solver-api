package uk.co.mruoc.cws.usecase;

import uk.co.mruoc.cws.entity.Attempt;

import java.util.Collection;

public interface HackathonClient {

    Collection<String> getPuzzleImageUrls();

    void recordAnswers(Attempt attempt);
}
