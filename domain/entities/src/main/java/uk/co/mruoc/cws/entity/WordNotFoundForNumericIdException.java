package uk.co.mruoc.cws.entity;

public class WordNotFoundForNumericIdException extends RuntimeException {

  public WordNotFoundForNumericIdException(int id) {
    super(String.format("word not found for numeric id %s", id));
  }
}
