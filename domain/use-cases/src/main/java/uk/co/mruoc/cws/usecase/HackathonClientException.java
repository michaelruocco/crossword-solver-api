package uk.co.mruoc.cws.usecase;

import java.util.UUID;

public class HackathonClientException extends RuntimeException {

  public HackathonClientException(UUID attemptId) {
    super(String.format("unable to record result for attempt %s", attemptId));
  }

  public HackathonClientException(String message) {
    super(message);
  }
}
