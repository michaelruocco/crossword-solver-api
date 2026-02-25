package uk.co.mruoc.cws.entity;

import java.time.Instant;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AttemptSummary {

  private final UUID id;
  private final Instant createdAt;
  private final long clueCount;
  private final long answerCount;
  private final boolean solving;
}
