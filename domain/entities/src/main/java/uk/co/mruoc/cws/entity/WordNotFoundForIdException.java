package uk.co.mruoc.cws.entity;

public class WordNotFoundForIdException extends RuntimeException {

  public WordNotFoundForIdException(Id id) {
    super(String.format("word not found for id %s", id));
  }
}
