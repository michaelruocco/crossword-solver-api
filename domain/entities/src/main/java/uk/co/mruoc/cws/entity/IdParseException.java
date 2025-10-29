package uk.co.mruoc.cws.entity;

public class IdParseException extends RuntimeException {

  public IdParseException(String value, Throwable cause) {
    super(String.format("could not parse id value %s", value), cause);
  }
}
