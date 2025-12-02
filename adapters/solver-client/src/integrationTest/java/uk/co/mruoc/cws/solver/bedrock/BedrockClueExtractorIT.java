package uk.co.mruoc.cws.solver.bedrock;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.co.mruoc.cws.solver.bedrock.BedrockRuntimeClientFactory.buildClueExtractor;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import uk.co.mruoc.cws.solver.stub.StubClueExtractor;
import uk.co.mruoc.cws.usecase.ClueExtractor;
import uk.co.mruoc.junit.EnvVarsPresent;

@EnvVarsPresent(values = {"AWS_ACCESS_KEY_ID", "AWS_SECRET_ACCESS_KEY"})
@Slf4j
public class BedrockClueExtractorIT {

  private final ClueExtractor extractor = buildClueExtractor();

  @Test
  void shouldExtractCluesFromPuzzleImage() {
    var imageUrl = "https://hackathon.caci.co.uk/images/puzzle3.png";

    var clues = extractor.extractClues(imageUrl);

    clues.forEach(clue -> log.info(clue.toString()));
    var expectedClues = new StubClueExtractor().extractClues(imageUrl);
    assertThat(clues).containsExactlyElementsOf(expectedClues);
  }
}
