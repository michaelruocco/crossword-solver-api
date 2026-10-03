package uk.co.mruoc.cws.solver;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.mruoc.cws.entity.Clue;

@RequiredArgsConstructor
@Slf4j
public class DefaultFindAnswerPromptTextFactory implements FindAnswerPromptTextFactory {

  private final String findCandidatesPromptTemplate;

  @Override
  public String toPromptText(Clue clue, int numberOfCandidates) {
    var promptText =
        populateClueIntoTemplate(findCandidatesPromptTemplate, clue)
            .replace("%NUMBER_OF_CANDIDATES%", Integer.toString(numberOfCandidates));
    log.debug("built find candidates prompt {}", promptText);
    return promptText;
  }

  private String populateClueIntoTemplate(String template, Clue clue) {
    return template
        .replace("%CLUE_ID%", clue.id().toString())
        .replace("%CLUE%", clue.text())
        .replace("%PATTERN%", clue.pattern());
  }
}
