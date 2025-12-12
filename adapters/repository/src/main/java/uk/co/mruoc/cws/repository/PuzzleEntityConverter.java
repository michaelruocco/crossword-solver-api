package uk.co.mruoc.cws.repository;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import uk.co.mruoc.cws.entity.Cell;
import uk.co.mruoc.cws.entity.Cells;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.entity.Clues;
import uk.co.mruoc.cws.entity.Coordinates;
import uk.co.mruoc.cws.entity.Id;
import uk.co.mruoc.cws.entity.Puzzle;
import uk.co.mruoc.cws.entity.Word;
import uk.co.mruoc.cws.entity.WordsFactory;
import uk.co.mruoc.cws.repository.entity.CellEntity;
import uk.co.mruoc.cws.repository.entity.ClueEntity;
import uk.co.mruoc.cws.repository.entity.PuzzleEntity;

@RequiredArgsConstructor
public class PuzzleEntityConverter {

  private final WordsFactory wordsFactory;

  public PuzzleEntityConverter() {
    this(new WordsFactory());
  }

  public Puzzle toPuzzle(PuzzleEntity entity) {
    var clues = toClues(entity.getClues());
    var cells = toCells(entity.getCells());
    return Puzzle.builder()
        .id(entity.getId())
        .name(entity.getName())
        .hash(entity.getHash())
        .clues(clues)
        .words(wordsFactory.toWords(clues, cells))
        .build();
  }

  public PuzzleEntity toEntity(Puzzle puzzle) {
    var entity = new PuzzleEntity();
    entity.setId(puzzle.getId());
    entity.setName(puzzle.getName());
    entity.setHash(puzzle.getHash());
    entity.setClues(toClueEntities(puzzle));
    entity.setCells(toCellEntities(puzzle));
    return entity;
  }

  private Clues toClues(Collection<ClueEntity> entities) {
    return new Clues(entities.stream().map(this::toClue).toList());
  }

  private Clue toClue(ClueEntity entity) {
    return Clue.builder()
        .id(new Id(entity.getClueId()))
        .text(entity.getText())
        .lengths(entity.getLengths())
        .build();
  }

  private Cells toCells(Collection<CellEntity> entities) {
    return new Cells(entities.stream().map(this::toCell).toList());
  }

  private Cell toCell(CellEntity entity) {
    return new Cell(entity.getCellId(), new Coordinates(entity.getX(), entity.getY()));
  }

  private Collection<ClueEntity> toClueEntities(Puzzle puzzle) {
    return puzzle.getClues().stream().map(clue -> toEntity(puzzle, clue)).toList();
  }

  private ClueEntity toEntity(Puzzle puzzle, Clue clue) {
    var entity = new ClueEntity();
    entity.setPuzzleId(puzzle.getId());
    entity.setClueId(clue.id().toString());
    entity.setText(clue.text());
    entity.setLengths(clue.lengths());
    return entity;
  }

  private Collection<CellEntity> toCellEntities(Puzzle puzzle) {
    return puzzle.getWords().stream()
        .collect(Collectors.toMap(Word::getNumericId, Function.identity(), (w1, w2) -> w1))
        .values()
        .stream()
        .map(word -> toEntity(puzzle, word))
        .toList();
  }

  private CellEntity toEntity(Puzzle puzzle, Word word) {
    var entity = new CellEntity();
    entity.setPuzzleId(puzzle.getId());
    entity.setCellId(word.getNumericId());
    entity.setX(word.x());
    entity.setY(word.y());
    return entity;
  }
}
