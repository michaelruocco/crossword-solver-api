package uk.co.mruoc.cws.solver.stub;

import lombok.RequiredArgsConstructor;
import uk.co.mruoc.cws.entity.Clues;
import uk.co.mruoc.cws.usecase.ClueRanker;

@RequiredArgsConstructor
public class StubClueRanker implements ClueRanker {

  @Override
  public Clues rankByEase(Clues clues) {
    return clues.sortByIds();
  }
}
