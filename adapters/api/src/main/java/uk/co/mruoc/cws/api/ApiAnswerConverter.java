package uk.co.mruoc.cws.api;

import uk.co.mruoc.cws.entity.Answer;
import uk.co.mruoc.cws.entity.Id;

public class ApiAnswerConverter {

  public Answer toAnswer(ApiAnswer apiAnswer) {
    return Answer.builder()
        .id(new Id(apiAnswer.getId(), apiAnswer.getDirection()))
        .value(apiAnswer.getValue())
        .confidenceScore(100)
        .confirmed(true)
        .build();
  }
}
