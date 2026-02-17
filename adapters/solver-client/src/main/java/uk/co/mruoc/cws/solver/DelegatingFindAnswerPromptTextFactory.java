package uk.co.mruoc.cws.solver;

import static uk.co.mruoc.cws.entity.ClueType.CRYPTIC;
import static uk.co.mruoc.cws.entity.ClueType.STANDARD;
import static uk.co.mruoc.file.FileLoader.loadContentFromClasspath;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.entity.ClueType;
import uk.co.mruoc.cws.entity.Clues;

@RequiredArgsConstructor
@Slf4j
public class DelegatingFindAnswerPromptTextFactory implements FindAnswerPromptTextFactory {

  private final FindAnswerPromptTextFactory standardFactory;
  private final FindAnswerPromptTextFactory crypticFactory;

  public DelegatingFindAnswerPromptTextFactory() {
    this(build(STANDARD), build(CRYPTIC));
  }

  @Override
  public String toPromptText(Clue clue, int numberOfCandidates) {
    return selectFactory(clue.type()).toPromptText(clue, numberOfCandidates);
  }

  @Override
  public String toPromptText(Clue clue) {
    return selectFactory(clue.type()).toPromptText(clue);
  }

  @Override
  public String toPromptText(Clues clues) {
    var type = clues.forceGetType();
    return selectFactory(type).toPromptText(clues);
  }

  private FindAnswerPromptTextFactory selectFactory(ClueType type) {
    log.debug("selecting {} find answer prompt text factory", type);
    if (type == CRYPTIC) {
      return crypticFactory;
    }
    return standardFactory;
  }

  public static FindAnswerPromptTextFactory build(ClueType type) {
    return DefaultFindAnswerPromptTextFactory.builder()
        .clueListConverter(new ClueListConverter())
        .findAnswerPromptSingleTemplate(loadTemplate("prompts/find-answer-%s-single.txt", type))
        .findAnswerPromptBatchTemplate(loadTemplate("prompts/find-answer-%s-batch.txt", type))
        .findCandidatesPromptTemplate(loadTemplate("prompts/find-candidates-%s.txt", type))
        .build();
  }

  private static String loadTemplate(String path, ClueType type) {
    return loadContentFromClasspath(String.format(path, type.name().toLowerCase()));
  }
}
