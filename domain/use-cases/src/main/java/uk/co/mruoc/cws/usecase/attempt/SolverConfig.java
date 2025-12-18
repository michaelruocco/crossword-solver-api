package uk.co.mruoc.cws.usecase.attempt;

import lombok.Builder;

@Builder
public record SolverConfig(int maxCandidatesPerClue, int maxDepth) {

  public SolverConfig() {
    this(10, 55);
  }
}
