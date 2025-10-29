package uk.co.mruoc.cws.entity;

public class DirectionIdNotSupportedException extends RuntimeException {

  public DirectionIdNotSupportedException(char id) {
    super(String.format("direction %s not supported", id));
  }
}
