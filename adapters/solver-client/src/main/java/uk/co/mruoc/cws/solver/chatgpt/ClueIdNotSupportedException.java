package uk.co.mruoc.cws.solver.chatgpt;

public class ClueIdNotSupportedException extends RuntimeException {

  public ClueIdNotSupportedException(String clueId) {
    super(String.format("text id %s not supported", clueId));
  }
}
