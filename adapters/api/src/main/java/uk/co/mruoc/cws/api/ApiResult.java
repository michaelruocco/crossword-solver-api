package uk.co.mruoc.cws.api;

import java.util.Collection;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResult {
  private final ApiAttempt attempt;
  private final int totalCount;
  private final int correctCount;
  private final double percentageCorrect;
  private final Collection<ApiAnswer> incorrectAnswers;
}
