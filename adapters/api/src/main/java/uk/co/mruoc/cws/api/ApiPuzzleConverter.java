package uk.co.mruoc.cws.api;

import java.util.Collection;
import uk.co.mruoc.cws.entity.Answer;
import uk.co.mruoc.cws.entity.Attempt;
import uk.co.mruoc.cws.entity.Cell;
import uk.co.mruoc.cws.entity.Cells;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.entity.Clues;
import uk.co.mruoc.cws.entity.Grid;
import uk.co.mruoc.cws.entity.Puzzle;

public class ApiPuzzleConverter {

  public ApiPuzzle<ApiClue> toApiPuzzle(Puzzle puzzle) {
    return ApiPuzzle.<ApiClue>builder()
        .id(puzzle.getId())
        .name(puzzle.getName())
        .hash(puzzle.getHash())
        .clues(toApiClues(puzzle.getClues()))
        .grid(toApiGrid(puzzle.getGrid()))
        .build();
  }

  public ApiPuzzle<ApiAttemptClue> toApiAttemptPuzzle(Attempt attempt) {
    var puzzle = attempt.puzzle();
    return ApiPuzzle.<ApiAttemptClue>builder()
        .id(puzzle.getId())
        .name(puzzle.getName())
        .hash(puzzle.getHash())
        .clues(toApiAttemptClues(attempt))
        .build();
  }

  private Collection<ApiClue> toApiClues(Clues clues) {
    return clues.stream().map(this::toApiClue).toList();
  }

  private ApiClue toApiClue(Clue clue) {
    return ApiClue.builder()
        .id(clue.numericId())
        .direction(clue.direction())
        .text(clue.text())
        .lengths(clue.lengths())
        .build();
  }

  private ApiGrid toApiGrid(Grid grid) {
    return ApiGrid.builder()
        .cells(toApiCells(grid.cells()))
        .columnWidth(grid.columnWidth())
        .rowHeight(grid.rowHeight())
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
        .build();
  }

  private Collection<ApiAttemptClue> toApiAttemptClues(Attempt attempt) {
    return attempt.getClues().stream().map(clue -> toApiAttemptClue(clue, attempt)).toList();
  }

  private ApiAttemptClue toApiAttemptClue(Clue clue, Attempt attempt) {
    var apiClue = toApiClue(clue);
    var builder = ApiAttemptClue.builder().clue(apiClue);
    attempt.getAnswer(clue.id()).map(this::toApiAttemptClueAnswer).ifPresent(builder::answer);
    return builder.build();
  }

  private ApiAttemptClueAnswer toApiAttemptClueAnswer(Answer answer) {
    return ApiAttemptClueAnswer.builder()
        .value(answer.value())
        .confidenceScore(answer.confidenceScore())
        .confirmed(answer.confirmed())
        .build();
  }
}
