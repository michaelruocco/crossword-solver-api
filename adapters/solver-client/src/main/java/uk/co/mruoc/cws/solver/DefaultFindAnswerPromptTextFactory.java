package uk.co.mruoc.cws.solver;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.entity.Clues;

@Builder
@Slf4j
public class DefaultFindAnswerPromptTextFactory implements FindAnswerPromptTextFactory {

  private final ClueListConverter clueListConverter;
  private final String findAnswerPromptSingleTemplate;
  private final String findAnswerPromptBatchTemplate;
  private final String findCandidatesPromptTemplate;

  @Override
  public String toPromptText(Clue clue, int numberOfCandidates) {
    var promptText =
        populateClueIntoTemplate(findCandidatesPromptTemplate, clue)
            .replace("%NUMBER_OF_CANDIDATES%", Integer.toString(numberOfCandidates));
    log.debug("built find candidates prompt {}", promptText);
    return promptText;
  }

  @Override
  public String toPromptText(Clue clue) {
    var promptText = populateClueIntoTemplate(findAnswerPromptSingleTemplate, clue);
    log.debug("built find single answer prompt {}", promptText);
    return promptText;
  }

  @Override
  public String toPromptText(Clues clues) {
    var clueList = clueListConverter.toClueList(clues);
    var promptText = findAnswerPromptBatchTemplate.replace("%CLUE_LIST%", clueList);
    log.debug("built find batch answer prompt {}", promptText);
    return promptText;
  }

  private String populateClueIntoTemplate(String template, Clue clue) {
    return template
        .replace("%CLUE_ID%", clue.id().toString())
        .replace("%CLUE%", clue.text())
        .replace("%PATTERN%", clue.pattern());
  }
}
