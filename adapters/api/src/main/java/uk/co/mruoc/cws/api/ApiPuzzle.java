package uk.co.mruoc.cws.api;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ApiPuzzle {
  private final long id;
  private final String name;
  private final String hash;
  private final ApiClues clues;
  private final ApiGrid grid;
}
