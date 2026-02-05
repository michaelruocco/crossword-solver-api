package uk.co.mruoc.cws.solver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.mruoc.cws.entity.Clues;

@RequiredArgsConstructor
@Slf4j
public class ClueExtractorResponseConverter {

  private final CrosswordJsonMapper mapper;

  public ClueExtractorResponseConverter() {
    this(new CrosswordJsonMapper());
  }

  public Clues toClues(String json) {
    log.info("got clues {}", json);
    return mapper.toClues(json);
  }
}
