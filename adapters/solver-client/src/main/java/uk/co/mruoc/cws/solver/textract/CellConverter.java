package uk.co.mruoc.cws.solver.textract;

import java.util.Collection;
import java.util.Comparator;
import uk.co.mruoc.cws.entity.Coordinates;
import uk.co.mruoc.cws.entity.Id;
import uk.co.mruoc.cws.entity.Word;
import uk.co.mruoc.cws.entity.Words;

public class CellConverter {

  public Words toWords(Collection<Cell> cells) {
    return new Words(
        cells.stream()
            .map(this::toWord)
            .sorted(Comparator.comparingInt(Word::getNumericId))
            .toList());
  }

  private Word toWord(Cell cell) {
    return Word.builder()
        .id(new Id(cell.number(), null))
        .coordinates(new Coordinates(cell.column(), cell.row()))
        .length(-1)
        .build();
  }
}
