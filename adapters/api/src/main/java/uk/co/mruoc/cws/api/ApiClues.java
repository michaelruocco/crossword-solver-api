package uk.co.mruoc.cws.api;

import java.util.Collection;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ApiClues {
  private final Collection<ApiClue> across;
  private final Collection<ApiClue> down;
}
