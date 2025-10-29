package uk.co.mruoc.cws.usecase;

import uk.co.mruoc.cws.entity.Answer;
import uk.co.mruoc.cws.entity.Answers;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.entity.Clues;

public interface AnswerFinder {

  Answers findAnswers(Clues clues);

  Answer findAnswer(Clue clue);
}
