package uk.co.mruoc.cws.solver.stub;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import uk.co.mruoc.cws.entity.Clues;
import uk.co.mruoc.cws.entity.Id;
import uk.co.mruoc.cws.usecase.ClueSorter;

@RequiredArgsConstructor
public class StubClueSorter implements ClueSorter {

  private final Collection<Id> sortedIds;

  @Override
  public Clues sort(Clues clues) {
    return clues.sortByIds(sortedIds);
  }
}
