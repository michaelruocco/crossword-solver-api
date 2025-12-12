package uk.co.mruoc.cws.solver;

import static uk.co.mruoc.file.FileLoader.loadContentFromClasspath;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.entity.Clues;

@RequiredArgsConstructor
@Slf4j
public class FindAnswerPromptTextFactory {

  private final ClueListConverter clueListConverter;
  private final String findAnswerPromptSingleTemplate;
  private final String findAnswerPromptBatchTemplate;

  public FindAnswerPromptTextFactory() {
    this(
        new ClueListConverter(),
        loadContentFromClasspath("prompts/find-answer-standard-single.txt"),
        loadContentFromClasspath("prompts/find-answer-standard-batch.txt"));
  }

  public String toPromptText(Clue clue) {
    var promptText =
        findAnswerPromptSingleTemplate
            .replace("%CLUE_ID%", clue.id().toString())
            .replace("%CLUE%", clue.text())
            .replace("%PATTERN%", clue.pattern());
    log.debug("built find single answer prompt {}", promptText);
    return promptText;
  }

  public String toPromptText(Clues clues) {
    var clueList = clueListConverter.toClueList(clues);
    var promptText = findAnswerPromptBatchTemplate.replace("%CLUE_LIST%", clueList);
    log.debug("built find batch answer prompt {}", promptText);
    return promptText;
  }
}
