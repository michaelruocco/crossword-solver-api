package uk.co.mruoc.cws.api;

import java.util.Collection;
import uk.co.mruoc.cws.entity.Answer;
import uk.co.mruoc.cws.entity.Attempt;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.entity.Puzzle;
import uk.co.mruoc.cws.entity.Words;

public class ApiPuzzleConverter {

  public ApiPuzzle<ApiClue> toApiPuzzle(Puzzle puzzle) {
    return ApiPuzzle.<ApiClue>builder()
        .id(puzzle.getId())
        .imageUrl(puzzle.getImageUrl())
        .clues(toApiClues(puzzle))
        .build();
  }

  public ApiPuzzle<ApiAttemptClue> toApiAttemptPuzzle(Attempt attempt) {
    var puzzle = attempt.puzzle();
    return ApiPuzzle.<ApiAttemptClue>builder()
        .id(puzzle.getId())
        .imageUrl(puzzle.getImageUrl())
        .clues(toApiAttemptClues(attempt))
        .build();
  }

  private Collection<ApiClue> toApiClues(Puzzle puzzle) {
    return puzzle.getClues().stream().map(clue -> toApiClue(clue, puzzle.getWords())).toList();
  }

  private ApiClue toApiClue(Clue clue, Words words) {
    return ApiClue.builder()
        .id(clue.numericId())
        .direction(clue.direction())
        .coordinates(words.getCoordinates(clue.numericId()))
        .text(clue.text())
        .lengths(clue.lengths())
        .build();
  }

  private Collection<ApiAttemptClue> toApiAttemptClues(Attempt attempt) {
    return attempt.getClues().stream().map(clue -> toApiAttemptClue(clue, attempt)).toList();
  }

  private ApiAttemptClue toApiAttemptClue(Clue clue, Attempt attempt) {
    var apiClue = toApiClue(clue, attempt.getWords());
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
