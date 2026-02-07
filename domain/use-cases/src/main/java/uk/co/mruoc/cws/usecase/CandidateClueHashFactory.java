package uk.co.mruoc.cws.usecase;

import lombok.RequiredArgsConstructor;
import uk.co.mruoc.cws.entity.Clue;

@RequiredArgsConstructor
public class CandidateClueHashFactory {

  private final HashFactory hashFactory;

  public CandidateClueHashFactory() {
    this(new HashFactory());
  }

  public String toId(Clue clue) {
    var id = String.format("%s-%s", clue.text(), clue.pattern());
    return hashFactory.toHash(id);
  }
}
