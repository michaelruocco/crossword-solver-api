package uk.co.mruoc.cws.solver;

import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.entity.Clues;

public interface FindAnswerPromptTextFactory {
  String toPromptText(Clue clue, int numberOfCandidates);

  String toPromptText(Clue clue);

  String toPromptText(Clues clues);
}
