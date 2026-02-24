package uk.co.mruoc.cws.repository.entity;

import java.time.Instant;
import java.util.UUID;

public interface AttemptSummaryProjection {

  UUID getId();

  Instant getCreatedAt();

  boolean isSolving();

  long getAnswerCount();

  long getClueCount();
}
