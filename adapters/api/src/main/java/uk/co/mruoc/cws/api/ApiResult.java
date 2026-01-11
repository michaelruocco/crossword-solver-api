package uk.co.mruoc.cws.api;

import lombok.Builder;
import lombok.Data;

import java.util.Collection;

@Data
@Builder
public class ApiResult {
    private final ApiAttempt attempt;
    private final int totalCount;
    private final int correctCount;
    private final double percentageCorrect;
    private final Collection<ApiAnswer> incorrectAnswers;
}
