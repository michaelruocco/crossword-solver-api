package uk.co.mruoc.cws.repository.entity;

import java.time.Instant;
import java.util.UUID;

public interface PuzzleSummaryProjection {

  UUID getId();

  String getName();

  Instant getCreatedAt();

  long getAttemptCount();
}
