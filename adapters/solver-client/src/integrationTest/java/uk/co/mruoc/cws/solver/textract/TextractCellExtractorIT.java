package uk.co.mruoc.cws.solver.textract;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.co.mruoc.cws.solver.bedrock.TextractClientFactory.buildCellExtractor;

import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.co.mruoc.cws.solver.stub.StubCellExtractor;
import uk.co.mruoc.cws.usecase.CellExtractor;
import uk.co.mruoc.junit.EnvVarsPresent;

@EnvVarsPresent(values = {"AWS_ACCESS_KEY_ID", "AWS_SECRET_ACCESS_KEY"})
@Slf4j
class TextractCellExtractorIT {

  private final CellExtractor extractor = buildCellExtractor();

  @ParameterizedTest
  @MethodSource("imageUrls")
  void shouldExtractCellsFromImage(String imageUrl) {
    var cells = extractor.extractCells(imageUrl);

    cells.forEach(word -> log.info(word.toString()));
    var expectedCells = new StubCellExtractor().extractCells(imageUrl);
    assertThat(cells).containsExactlyElementsOf(expectedCells);
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
        .filter(a -> a.get()[0].toString().contains("puzzle5."));
  }

  private static String toUrl(String filename) {
    return String.format("https://hackathon.caci.co.uk/images/%s", filename);
  }
}
