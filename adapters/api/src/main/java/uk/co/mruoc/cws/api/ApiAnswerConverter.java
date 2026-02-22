package uk.co.mruoc.cws.api;

import java.util.Collection;
import uk.co.mruoc.cws.entity.Answer;
import uk.co.mruoc.cws.entity.Answers;
import uk.co.mruoc.cws.entity.Id;

public class ApiAnswerConverter {

  public Answer toAnswer(ApiAnswer apiAnswer) {
    return Answer.builder()
        .id(new Id(apiAnswer.getId()))
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
    apiAnswer.setId(answer.id().toString());
    apiAnswer.setValue(answer.value());
    return apiAnswer;
  }
}
