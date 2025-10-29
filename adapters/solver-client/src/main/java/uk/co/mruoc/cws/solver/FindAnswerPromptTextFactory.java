package uk.co.mruoc.cws.solver;

import static uk.co.mruoc.file.FileLoader.loadContentFromClasspath;

import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.entity.Clues;

@RequiredArgsConstructor
@Slf4j
public class FindAnswerPromptTextFactory {

  private final String findAnswerPromptSingleTemplate;
  private final String findAnswerPromptBatchTemplate;

  public FindAnswerPromptTextFactory() {
    this(
        loadContentFromClasspath("prompts/find-answer-prompt-standard-single.txt"),
        loadContentFromClasspath("prompts/find-answer-prompt-standard-batch.txt"));
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
    var clueList = toClueList(clues);
    var promptText = findAnswerPromptBatchTemplate.replace("%CLUE_LIST%", clueList);
    log.debug("built find batch answer prompt {}", promptText);
    return promptText;
  }

  private String toClueList(Clues clues) {
    return clues.stream().map(this::toString).collect(Collectors.joining(System.lineSeparator()));
  }

  private String toString(Clue clue) {
    return String.format("%s: %s | %s", clue.id(), clue.text(), clue.pattern());
  }
}
