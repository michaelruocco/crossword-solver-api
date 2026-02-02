package uk.co.mruoc.cws.solver.textract;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.co.mruoc.cws.solver.stub.StubCellExtractor;
import uk.co.mruoc.cws.usecase.CellExtractor;
import uk.co.mruoc.cws.usecase.ImageDownloader;
import uk.co.mruoc.cws.usecase.StubImageDownloader;

@Slf4j
class TesseractCellExtractorIT {

  private final ImageDownloader downloader = new StubImageDownloader();

  private final CellExtractor extractor = new TesseractCellExtractor();

  @ParameterizedTest
  @MethodSource("imageUrls")
  void shouldExtractCellsFromImage(String imageUrl) {
    var image = downloader.downloadImage(imageUrl);

    var cells = extractor.extractCells(image);

    cells.forEach(cell -> log.info(cell.toString()));
    var expectedCells = new StubCellExtractor().extractCells(image);
    assertThat(cells).containsExactlyInAnyOrderElementsOf(expectedCells);
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
        Arguments.of(toUrl("puzzle24.jpg")));
  }

  private static String toUrl(String filename) {
    return String.format("https://hackathon.caci.co.uk/images/%s", filename);
  }
}
