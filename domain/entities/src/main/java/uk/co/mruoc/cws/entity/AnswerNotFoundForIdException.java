package uk.co.mruoc.cws.entity;

public class AnswerNotFoundForIdException extends RuntimeException {

  public AnswerNotFoundForIdException(Id id) {
    super(String.format("answer not found for id %s", id));
  }
}
