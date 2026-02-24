package uk.co.mruoc.cws.api;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import uk.co.mruoc.cws.entity.Attempt;
import uk.co.mruoc.cws.entity.AttemptSummary;

@RequiredArgsConstructor
public class ApiAttemptConverter {

  private final ApiPuzzleConverter puzzleConverter;

  public ApiAttemptConverter() {
    this(new ApiPuzzleConverter());
  }

  public Collection<ApiAttemptSummary> toApiSummaries(Collection<AttemptSummary> summaries) {
    return summaries.stream().map(this::toApiSummary).toList();
  }

  public ApiAttemptSummary toApiSummary(AttemptSummary summary) {
    return ApiAttemptSummary.builder()
        .id(summary.getId())
        .createdAt(summary.getCreatedAt())
        .solving(summary.isSolving())
        .answerCount(summary.getAnswerCount())
        .clueCount(summary.getClueCount())
        .build();
  }

  public ApiAttempt toApiAttempt(Attempt attempt) {
    return ApiAttempt.builder()
        .id(attempt.id())
        .createdAt(attempt.createdAt())
        .solving(attempt.solving())
        .puzzle(puzzleConverter.toApiPuzzle(attempt))
        .build();
  }
}
