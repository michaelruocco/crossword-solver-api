package uk.co.mruoc.cws.api;

import uk.co.mruoc.cws.entity.Answer;
import uk.co.mruoc.cws.entity.Answers;
import uk.co.mruoc.cws.entity.Id;

import java.util.Collection;

public class ApiAnswerConverter {

  public Answer toAnswer(ApiAnswer apiAnswer) {
    return Answer.builder()
        .id(new Id(apiAnswer.getId(), apiAnswer.getDirection()))
        .value(apiAnswer.getValue())
        .confidenceScore(100)
        .confirmed(true)
        .build();
  }

  public Collection<ApiAnswer> toApiAnswers(Answers answers) {
    return answers.stream().map(this::toApiAnswer).toList();
  }

  private ApiAnswer toApiAnswer(Answer answer) {
    var apiAnswer = new ApiAnswer();
    apiAnswer.setId(answer.numericId());
    apiAnswer.setDirection(answer.direction());
    apiAnswer.setValue(answer.value());
    return apiAnswer;
  }
}
