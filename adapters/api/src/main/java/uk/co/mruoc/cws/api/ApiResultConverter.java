package uk.co.mruoc.cws.api;

import lombok.RequiredArgsConstructor;
import uk.co.mruoc.cws.entity.Result;

@RequiredArgsConstructor
public class ApiResultConverter {

  private final ApiAttemptConverter attemptConverter;
  private final ApiAnswerConverter answerConverter;

  public ApiResultConverter() {
    this(new ApiAttemptConverter(), new ApiAnswerConverter());
  }

  public ApiResult toApiResult(Result result) {
    return ApiResult.builder()
        .attempt(attemptConverter.toApiAttempt(result.getAttempt()))
        .totalCount(result.getTotalCount())
        .correctCount(result.getCorrectCount())
        .percentageCorrect(result.percentageCorrect())
        .incorrectAnswers(answerConverter.toApiAnswers(result.getIncorrectAnswers()))
        .build();
  }
}
