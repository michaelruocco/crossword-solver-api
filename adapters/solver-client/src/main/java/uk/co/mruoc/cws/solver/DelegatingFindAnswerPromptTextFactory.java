package uk.co.mruoc.cws.solver;

import static uk.co.mruoc.cws.entity.ClueType.CRYPTIC;
import static uk.co.mruoc.cws.entity.ClueType.STANDARD;
import static uk.co.mruoc.file.FileLoader.loadContentFromClasspath;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.entity.ClueType;

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
        .findCandidatesPromptTemplate(loadTemplate(type))
        .build();
  }

  private static String loadTemplate(ClueType type) {
    return loadContentFromClasspath(
        String.format("prompts/find-candidates-%s.txt", type.name().toLowerCase()));
  }
}
