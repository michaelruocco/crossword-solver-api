package uk.co.mruoc.cws.entity;

import java.util.Comparator;

public class WordsFactory {

  public Words toWords(Clues clues, Cells cells) {
    return new Words(
        clues.stream()
            .map(clue -> toWord(clue, cells))
            .sorted(Comparator.comparingInt(Word::getNumericId))
            .toList());
  }

  private Word toWord(Clue clue, Cells cells) {
    return Word.builder()
        .id(clue.id())
        .coordinates(cells.forceFindCoordinatesById(clue.numericId()))
        .length(clue.totalLength())
        .build();
  }
}
