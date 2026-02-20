package uk.co.mruoc.cws.api;

import java.util.Collection;
import uk.co.mruoc.cws.api.ApiPuzzle.ApiPuzzleBuilder;
import uk.co.mruoc.cws.entity.Answers;
import uk.co.mruoc.cws.entity.Attempt;
import uk.co.mruoc.cws.entity.Cell;
import uk.co.mruoc.cws.entity.Cells;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.entity.Clues;
import uk.co.mruoc.cws.entity.Grid;
import uk.co.mruoc.cws.entity.Id;
import uk.co.mruoc.cws.entity.Puzzle;
import uk.co.mruoc.cws.entity.PuzzleSummary;

public class ApiPuzzleConverter {

  public Collection<ApiPuzzleSummary> toApiSummaries(Collection<PuzzleSummary> summaries) {
    return summaries.stream().map(this::toApiSummary).toList();
  }

  public ApiPuzzle toApiPuzzle(Attempt attempt) {
    var puzzle = attempt.puzzle();
    var answers = attempt.getConfirmedAnswers();
    return toApiPuzzleBuilder(puzzle)
        .clues(addAnswers(toApiClues(puzzle.getClues()), answers))
        .grid(toApiGrid(attempt.getGrid()))
        .build();
  }

  public ApiPuzzle toApiPuzzle(Puzzle puzzle) {
    return toApiPuzzleBuilder(puzzle)
        .clues(toApiClues(puzzle.getClues()))
        .grid(toApiGrid(puzzle.getGrid()))
        .build();
  }

  private ApiPuzzleBuilder toApiPuzzleBuilder(Puzzle puzzle) {
    return ApiPuzzle.builder()
        .id(puzzle.getId())
        .name(puzzle.getName())
        .hash(puzzle.getHash())
        .createdAt(puzzle.getCreatedAt())
        .attemptCount(puzzle.getAttemptCount());
  }

  private ApiClues addAnswers(ApiClues clues, Answers answers) {
    return ApiClues.builder()
        .across(addAnswers(clues.getAcross(), answers))
        .down(addAnswers(clues.getDown(), answers))
        .build();
  }

  private Collection<ApiClue> addAnswers(Collection<ApiClue> clues, Answers answers) {
    return clues.stream().map(clue -> addAnswer(clue, answers)).toList();
  }

  private ApiClue addAnswer(ApiClue clue, Answers answers) {
    var id = new Id(clue.getId());
    return answers.findById(id).map(answer -> clue.withAnswer(answer.value())).orElse(clue);
  }

  private ApiPuzzleSummary toApiSummary(PuzzleSummary summary) {
    return ApiPuzzleSummary.builder()
        .id(summary.getId())
        .name(summary.getName())
        .createdAt(summary.getCreatedAt())
        .attemptCount(summary.getAttemptCount())
        .build();
  }

  private ApiClues toApiClues(Clues clues) {
    return ApiClues.builder()
        .across(clues.getAcross().stream().map(this::toApiClue).toList())
        .down(clues.getDown().stream().map(this::toApiClue).toList())
        .build();
  }

  private ApiClue toApiClue(Clue clue) {
    return ApiClue.builder()
        .id(clue.id().toString())
        .text(clue.text())
        .lengths(clue.lengths())
        .build();
  }

  private ApiGrid toApiGrid(Grid grid) {
    return ApiGrid.builder()
        .cells(toApiCells(grid.cells()))
        .rowCount(grid.numberOfRows())
        .columnCount(grid.numberOfColumns())
        .build();
  }

  private Collection<ApiCell> toApiCells(Cells cells) {
    return cells.stream().map(this::toApiCell).toList();
  }

  private ApiCell toApiCell(Cell cell) {
    return ApiCell.builder()
        .coordinates(cell.coordinates())
        .black(cell.black())
        .id(cell.getId().orElse(null))
        .letter(cell.getLetter().orElse(null))
        .build();
  }
}
