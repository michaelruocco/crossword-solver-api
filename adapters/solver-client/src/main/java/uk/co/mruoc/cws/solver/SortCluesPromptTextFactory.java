package uk.co.mruoc.cws.solver;

import static uk.co.mruoc.file.FileLoader.loadContentFromClasspath;

import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.entity.Clues;

@RequiredArgsConstructor
@Slf4j
public class SortCluesPromptTextFactory {

  private final String sortCluesPromptTemplate;

  public SortCluesPromptTextFactory() {
    this(loadContentFromClasspath("prompts/sort-clues-by-simplicity-prompt.txt"));
  }

  public String toPromptText(Clues clues) {
    var clueList = toClueList(clues);
    var promptText = sortCluesPromptTemplate.replaceAll("%CLUE_LIST%", clueList);
    log.info("built sort clues prompt {}", promptText);
    return promptText;
  }

  private String toClueList(Clues clues) {
    return clues.stream().map(this::toString).collect(Collectors.joining(System.lineSeparator()));
  }

  private String toString(Clue clue) {
    return String.format("%s: %s", clue.id(), clue.text());
  }
}
