package uk.co.mruoc.cws.repository.entity;

import java.util.UUID;

public interface PuzzleSummaryProjection {

  UUID getId();

  String getName();

  long getAttemptCount();
}
