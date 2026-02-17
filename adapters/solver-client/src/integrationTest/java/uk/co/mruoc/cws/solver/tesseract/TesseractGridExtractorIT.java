package uk.co.mruoc.cws.solver.tesseract;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.co.mruoc.cws.image.ImageUrlBuilder.toUrl;

import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.co.mruoc.cws.solver.stub.StubGridExtractor;
import uk.co.mruoc.cws.usecase.GridExtractor;
import uk.co.mruoc.cws.usecase.ImageDownloader;
import uk.co.mruoc.cws.usecase.StubImageDownloader;
import uk.co.mruoc.junit.TesseractInstalled;

@TesseractInstalled
@Slf4j
class TesseractGridExtractorIT {

  private final ImageDownloader downloader = new StubImageDownloader();

  private final GridExtractor extractor = new TesseractGridExtractor(NumberDetectorFactory.build());

  @ParameterizedTest
  @MethodSource("imageUrls")
  void shouldExtractGridFromImage(String imageUrl) {
    var image = downloader.downloadImage(imageUrl);

    var grid = extractor.extractGrid(image);

    var cells = grid.cells();
    cells.forEach(cell -> log.info(cell.toString()));
    var expectedGrid = new StubGridExtractor().extractGrid(image);
    assertThat(cells).containsExactlyInAnyOrderElementsOf(expectedGrid.cells());
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
}
