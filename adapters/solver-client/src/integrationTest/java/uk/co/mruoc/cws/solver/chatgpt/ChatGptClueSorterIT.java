package uk.co.mruoc.cws.solver.chatgpt;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import uk.co.mruoc.cws.solver.stub.StubClueExtractor;
import uk.co.mruoc.cws.usecase.ClueExtractor;
import uk.co.mruoc.cws.usecase.ClueSorter;
import uk.co.mruoc.junit.EnvVarsPresent;

@EnvVarsPresent(values = {"OPEN_AI_API_KEY"})
@Slf4j
public class ChatGptClueSorterIT {

  private final ClueExtractor clueExtractor = new StubClueExtractor();
  private final ClueSorter finder = ChatGptClientFactory.buildClueSorter();

  @Test
  void shouldFindBatchOfAnswers() {
    var clues = clueExtractor.extractClues("http://any-host/puzzle1.png");

    var sortedClues = finder.sort(clues);

    sortedClues.stream().forEach(clue -> log.info(clue.toString()));
    assertThat(sortedClues).containsExactlyInAnyOrderElementsOf(clues);
  }
}
