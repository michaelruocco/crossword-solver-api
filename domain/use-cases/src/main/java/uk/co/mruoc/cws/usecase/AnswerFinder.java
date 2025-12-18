package uk.co.mruoc.cws.usecase;

import uk.co.mruoc.cws.entity.Answer;
import uk.co.mruoc.cws.entity.Answers;
import uk.co.mruoc.cws.entity.Candidates;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.entity.Clues;

public interface AnswerFinder {

  default Candidates findCandidates(Clue clue, int numberOfCandidates) {
    return new Candidates(findAnswer(clue));
  }

  Answers findAnswers(Clues clues);

  Answer findAnswer(Clue clue);
}
