package uk.co.mruoc.cws.solver.bedrock;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.co.mruoc.cws.solver.bedrock.BedrockRuntimeClientFactory.buildClient;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import uk.co.mruoc.cws.solver.stub.StubClueExtractor;
import uk.co.mruoc.cws.usecase.ClueExtractor;
import uk.co.mruoc.cws.usecase.ImageDownloader;
import uk.co.mruoc.cws.usecase.StubImageDownloader;
import uk.co.mruoc.junit.EnvVarsPresent;

@EnvVarsPresent(values = {"AWS_ACCESS_KEY_ID", "AWS_SECRET_ACCESS_KEY"})
@Slf4j
public class BedrockClueExtractorIT {

  private final ImageDownloader downloader = new StubImageDownloader();
  private final ClueExtractor extractor = new BedrockClueExtractor(buildClient());

  @Test
  void shouldExtractCluesFromPuzzleImage() {
    var imageUrl = "https://hackathon.caci.co.uk/images/puzzle24.jpg";
    var image = downloader.downloadImage(imageUrl);

    var clues = extractor.extractClues(image);

    clues.forEach(clue -> log.info(clue.toString()));
    var expectedClues = new StubClueExtractor().extractClues(image);
    assertThat(clues).containsExactlyElementsOf(expectedClues);
  }
}
