package uk.co.mruoc.cws.solver;

import uk.co.mruoc.cws.entity.Clue;

public interface FindAnswerPromptTextFactory {
  String toPromptText(Clue clue, int numberOfCandidates);
}
