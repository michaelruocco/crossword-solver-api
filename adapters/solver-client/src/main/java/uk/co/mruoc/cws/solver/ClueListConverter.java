package uk.co.mruoc.cws.solver;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.entity.Clues;
import uk.co.mruoc.cws.entity.Id;

public class ClueListConverter {

  public String toClueList(Clues clues) {
    return clues.stream().map(this::toString).collect(Collectors.joining(System.lineSeparator()));
  }

  public Collection<Id> toIds(String clueList) {
    return Arrays.stream(clueList.split(System.lineSeparator())).map(this::toId).toList();
  }

  private String toString(Clue clue) {
    return String.format("%s: %s | %s", clue.id(), clue.text(), clue.pattern());
  }

  private Id toId(String line) {
    return new Id(line.split(":")[0]);
  }
}
