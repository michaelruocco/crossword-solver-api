package uk.co.mruoc.cws.usecase;

public class HackathonClientException extends RuntimeException {

  public HackathonClientException(long attemptId) {
    super(String.format("unable to record result for attempt %d", attemptId));
  }

  public HackathonClientException(String message) {
    super(message);
  }
}
