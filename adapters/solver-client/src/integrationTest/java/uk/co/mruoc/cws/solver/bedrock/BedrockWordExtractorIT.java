package uk.co.mruoc.cws.solver.bedrock;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.co.mruoc.cws.solver.bedrock.BedrockRuntimeClientFactory.buildWordExtractor;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import uk.co.mruoc.cws.solver.stub.StubWordExtractor;
import uk.co.mruoc.cws.usecase.WordExtractor;

@Slf4j
public class BedrockWordExtractorIT {

  private final WordExtractor extractor = buildWordExtractor();

  @Test
  void shouldExtractCluesFromPuzzleImage() {
    var imageUrl = "https://hackathon.caci.co.uk/images/puzzle1.png";

    var words = extractor.extractWords(imageUrl);

    words.getWords().forEach(word -> log.info(word.toString()));
    var expectedWords = new StubWordExtractor().extractWords(imageUrl);
    assertThat(words.getWords()).containsExactlyElementsOf(expectedWords.getWords());
  }
}
