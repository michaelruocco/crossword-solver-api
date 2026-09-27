package uk.co.mruoc.cws.usecase;

import uk.co.mruoc.cws.entity.Candidates;
import uk.co.mruoc.cws.entity.Clue;

public interface AnswerFinder {
  Candidates findCandidates(Clue clue, int numberOfCandidates);
}
