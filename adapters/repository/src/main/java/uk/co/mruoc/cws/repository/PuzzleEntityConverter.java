package uk.co.mruoc.cws.repository;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import uk.co.mruoc.cws.entity.Cell;
import uk.co.mruoc.cws.entity.Cells;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.entity.Clues;
import uk.co.mruoc.cws.entity.Coordinates;
import uk.co.mruoc.cws.entity.Grid;
import uk.co.mruoc.cws.entity.Id;
import uk.co.mruoc.cws.entity.Puzzle;
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
        .format(entity.getFormat())
        .hash(entity.getHash())
        .clues(clues)
        .grid(new Grid(cells, entity.getColumnWidth(), entity.getRowHeight()))
        .words(wordsFactory.toWords(clues, cells))
        .build();
  }

  public PuzzleEntity toEntity(Puzzle puzzle) {
    var entity = new PuzzleEntity();
    entity.setId(puzzle.getId());
    entity.setName(puzzle.getName());
    entity.setFormat(puzzle.getFormat());
    entity.setHash(puzzle.getHash());
    entity.setClues(toClueEntities(puzzle));
    entity.setCells(toCellEntities(puzzle));
    var grid = puzzle.getGrid();
    entity.setColumnWidth(grid.columnWidth());
    entity.setRowHeight(grid.rowHeight());
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
    var coordinates = new Coordinates(entity.getX(), entity.getY());
    return new Cell(coordinates, entity.isBlack(), entity.getCellId().orElse(null));
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
    var grid = puzzle.getGrid();
    return grid.cells().stream().map(cell -> toEntity(puzzle, cell)).toList();
  }

  private CellEntity toEntity(Puzzle puzzle, Cell cell) {
    var entity = new CellEntity();
    entity.setPuzzleId(puzzle.getId());
    entity.setCellId(cell.getId().orElse(null));
    entity.setX(cell.x());
    entity.setY(cell.y());
    entity.setBlack(cell.black());
    return entity;
  }
}
