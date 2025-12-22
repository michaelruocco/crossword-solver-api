package uk.co.mruoc.cws.repository;

import lombok.RequiredArgsConstructor;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.usecase.HashFactory;

@RequiredArgsConstructor
public class CandidateClueEntityIdFactory {

  private final HashFactory hashFactory;

  public CandidateClueEntityIdFactory() {
    this(new HashFactory());
  }

  public String toId(Clue clue) {
    var id = String.format("%s-%s", clue.text(), clue.pattern());
    return hashFactory.toHash(id);
  }
}
