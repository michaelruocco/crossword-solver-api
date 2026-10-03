package uk.co.mruoc.cws.usecase;

import lombok.RequiredArgsConstructor;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.entity.ClueCandidateId;

@RequiredArgsConstructor
public class CandidateClueHashFactory {

  private final HashFactory hashFactory;

  public CandidateClueHashFactory() {
    this(new HashFactory());
  }

  public String toHash(Clue clue) {
    return toHash(clue.text(), clue.pattern());
  }

  public String toHash(ClueCandidateId id) {
    return toHash(id.text(), id.pattern());
  }

  private String toHash(String text, String pattern) {
    var id = String.format("%s-%s", text, pattern);
    return hashFactory.toHash(id);
  }
}
