package uk.co.mruoc.cws.api;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import uk.co.mruoc.cws.entity.Answer;
import uk.co.mruoc.cws.entity.Attempt;
import uk.co.mruoc.cws.entity.AttemptSummary;
import uk.co.mruoc.cws.entity.Puzzle;
import uk.co.mruoc.cws.entity.PuzzleSummary;
import uk.co.mruoc.cws.entity.Result;

@RequiredArgsConstructor
public class ApiConverter {

  private final ApiPuzzleConverter puzzleConverter;
  private final ApiAttemptConverter attemptConverter;
  private final ApiAnswerConverter answerConverter;
  private final ApiResultConverter resultConverter;

  public ApiConverter() {
    this(
        new ApiPuzzleConverter(),
        new ApiAttemptConverter(),
        new ApiAnswerConverter(),
        new ApiResultConverter());
  }

  public Collection<ApiPuzzleSummary> toApiPuzzleSummaries(Collection<PuzzleSummary> summaries) {
    return puzzleConverter.toApiSummaries(summaries);
  }

  public ApiPuzzle toApiPuzzle(Puzzle puzzle) {
    return puzzleConverter.toApiPuzzle(puzzle);
  }

  public Answer toAnswer(ApiAnswer apiAnswer) {
    return answerConverter.toAnswer(apiAnswer);
  }

  public Collection<ApiAttemptSummary> toApiAttemptSummaries(Collection<AttemptSummary> summaries) {
    return attemptConverter.toApiSummaries(summaries);
  }

  public ApiAttempt toApiAttempt(Attempt attempt) {
    return attemptConverter.toApiAttempt(attempt);
  }

  public ApiResult toApiResult(Result result) {
    return resultConverter.toApiResult(result);
  }
}
