package uk.co.mruoc.cws.solver.textract;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.co.mruoc.cws.usecase.ImageDownloader;
import uk.co.mruoc.cws.usecase.StubImageDownloader;

class GridDimensionsCalculatorIT {

  private final ImageDownloader downloader = new StubImageDownloader();
  private final ImageProcessor preprocessor = new ImageProcessor();
  private final GridDimensionsCalculator calculator = new GridDimensionsCalculator();

  @ParameterizedTest
  @MethodSource("imageUrlAndExpectedGridSize")
  void shouldCalculateNumberOfRowsAndColumnsInGridImages(
      String imageUrl, int expectedColumns, int expectedRows) {
    var image = downloader.downloadImage(imageUrl);
    var grid = preprocessor.extractGrid(image);
    var binary = preprocessor.process(image);

    var dimensions = calculator.calculateDimensions(binary).withGrid(grid);

    assertAll(
        () -> assertThat(dimensions.getNumberOfColumns()).isEqualTo(expectedColumns),
        () -> assertThat(dimensions.getNumberOfRows()).isEqualTo(expectedRows));
  }

  private static Stream<Arguments> imageUrlAndExpectedGridSize() {
    return Stream.of(
        Arguments.of(toUrl("puzzle1.png"), 13, 13),
        Arguments.of(toUrl("puzzle2.png"), 13, 13),
        Arguments.of(toUrl("puzzle3.png"), 13, 13),
        Arguments.of(toUrl("puzzle4.png"), 13, 13),
        Arguments.of(toUrl("puzzle5.png"), 13, 13),
        Arguments.of(toUrl("puzzle9.jpg"), 13, 29),
        Arguments.of(toUrl("puzzle14.jpg"), 11, 23),
        Arguments.of(toUrl("puzzle24.jpg"), 15, 15));
  }

  private static String toUrl(String filename) {
    return String.format("https://hackathon.caci.co.uk/images/%s", filename);
  }
}
