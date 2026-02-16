package uk.co.mruoc.cws.solver;

import static uk.co.mruoc.file.FileLoader.loadContentFromClasspath;

import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.entity.Clues;

@RequiredArgsConstructor
@Slf4j
public class DeterminePuzzleTypePromptTextFactory {

  private final String determinePuzzleTypeTemplate;

  public DeterminePuzzleTypePromptTextFactory() {
    this(loadContentFromClasspath("prompts/determine-puzzle-type.txt"));
  }

  public String toPromptText(Clues clues) {
    var clueList = toClueList(clues);
    var promptText = determinePuzzleTypeTemplate.replace("%CLUE_LIST%", clueList);
    log.debug("built determine puzzle type prompt {}", promptText);
    return promptText;
  }

  private String toClueList(Clues clues) {
    return clues.stream().map(Clue::text).collect(Collectors.joining(System.lineSeparator()));
  }
}
