package uk.co.mruoc.cws.solver;

import static uk.co.mruoc.file.FileLoader.loadContentFromClasspath;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.mruoc.cws.entity.Clues;

@RequiredArgsConstructor
@Slf4j
public class ClueRankerPromptTextFactory {

  private final ClueListConverter clueListConverter;
  private final String rankCluesTemplate;

  public ClueRankerPromptTextFactory() {
    this(new ClueListConverter(), loadContentFromClasspath("prompts/rank-clues.txt"));
  }

  public String toPromptText(Clues clues) {
    var clueList = clueListConverter.toClueList(clues);
    var promptText = rankCluesTemplate.replace("%CLUE_LIST%", clueList);
    log.debug("built rank clues prompt {}", promptText);
    return promptText;
  }
}
