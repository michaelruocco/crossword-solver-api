package uk.co.mruoc.cws.solver.bedrock;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.co.mruoc.cws.solver.bedrock.BedrockRuntimeClientFactory.buildClient;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
    var imageUrl = "https://hackathon.caci.co.uk/images/puzzle2.png";
    var image = downloader.downloadImage(imageUrl);

    var clues = extractor.extractClues(image);

    clues.forEach(clue -> log.info(clue.toString()));
    var expectedClues = new StubClueExtractor().extractClues(image);
    assertThat(clues).containsExactlyElementsOf(expectedClues);
  }

  @Disabled
  @ParameterizedTest
  @ValueSource(
      strings = {
        "https://hackathon.caci.co.uk/images/puzzle1.png",
        "https://hackathon.caci.co.uk/images/puzzle2.png",
        "https://hackathon.caci.co.uk/images/puzzle3.png",
        "https://hackathon.caci.co.uk/images/puzzle4.png",
        "https://hackathon.caci.co.uk/images/puzzle5.png",
        "https://hackathon.caci.co.uk/images/puzzle9.jpg",
        "https://hackathon.caci.co.uk/images/puzzle14.jpg",
        "https://hackathon.caci.co.uk/images/puzzle24.jpg",
      })
  void shouldExtractCluesFromAllPuzzleImages(String imageUrl) {
    var image = downloader.downloadImage(imageUrl);

    var clues = extractor.extractClues(image);

    clues.forEach(clue -> log.info(clue.toString()));
    var expectedClues = new StubClueExtractor().extractClues(image);
    assertThat(clues).containsExactlyElementsOf(expectedClues);
  }
}
