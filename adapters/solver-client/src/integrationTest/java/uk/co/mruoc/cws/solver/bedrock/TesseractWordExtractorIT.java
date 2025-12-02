package uk.co.mruoc.cws.solver.bedrock;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.co.mruoc.cws.solver.stub.StubWordExtractor;
import uk.co.mruoc.cws.usecase.StubImageDownloader;
import uk.co.mruoc.cws.usecase.WordExtractor;

@Slf4j
class TesseractWordExtractorIT {

  private final WordExtractor extractor =
      new TesseractWordExtractor(new StubImageDownloader(), new TesseractFactory().build());

  @ParameterizedTest
  @MethodSource("imageUrls")
  void shouldExtractWordsFromImage(String imageUrl) {
    var words = extractor.extractWords(imageUrl);

    words.getWords().forEach(word -> log.info(word.toString()));
    var expectedWords = new StubWordExtractor().extractWords(imageUrl);
    assertThat(words.getWords()).containsExactlyElementsOf(expectedWords.getWords());
  }

  private static Stream<Arguments> imageUrls() {
    return Stream.of(
            Arguments.of(toUrl("puzzle1.png")),
            Arguments.of(toUrl("puzzle2.png")),
            Arguments.of(toUrl("puzzle3.png")),
            Arguments.of(toUrl("puzzle4.png")),
            Arguments.of(toUrl("puzzle5.png")),
            Arguments.of(toUrl("puzzle9.jpg")),
            Arguments.of(toUrl("puzzle14.jpg")),
            Arguments.of(toUrl("puzzle24.jpg")))
        .filter(a -> a.get()[0].toString().contains("puzzle2."));
  }

  private static String toUrl(String filename) {
    return String.format("https://hackathon.caci.co.uk/images/%s", filename);
  }
}
