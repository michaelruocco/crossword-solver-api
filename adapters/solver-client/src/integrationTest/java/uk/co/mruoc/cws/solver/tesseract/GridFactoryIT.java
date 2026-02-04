package uk.co.mruoc.cws.solver.tesseract;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.co.mruoc.cws.image.ImageLoader.loadImage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.mruoc.cws.usecase.UrlConverter;

@Slf4j
class GridFactoryIT {

  private static final ImageDirectory OUTPUT_DIRECTORY =
      new ImageDirectory("integration-test-files/output-grid");

  private final GridFactory factory = new GridFactory();
  private final GridImageFactory imageFactory = new GridImageFactory();
  private final UrlConverter urlConverter = new UrlConverter();

  @BeforeAll
  static void initOutputDirectory() {
    OUTPUT_DIRECTORY.init();
  }

  @AfterAll
  static void deleteOutputDirectory() {
    // OUTPUT_DIRECTORY.delete();
    MatLogger.deleteAll();
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "/examples/puzzle1/puzzle1.png",
        "/examples/puzzle2/puzzle2.png",
        "/examples/puzzle3/puzzle3.png",
        "/examples/puzzle4/puzzle4.png",
        "/examples/puzzle5/puzzle5.png",
        "/examples/puzzle9/puzzle9.jpg",
        "/examples/puzzle14/puzzle14.jpg",
        "/examples/puzzle24/puzzle24.jpg"
      })
  void shouldExtractCellsFromImage(String puzzlePath) {
    var puzzleName = toFilenameExcludingExtension(puzzlePath);
    var puzzle = loadImage(puzzlePath);

    var grid = factory.toGrid(puzzle);

    var image = imageFactory.toImage(grid);
    var outputPath = OUTPUT_DIRECTORY.writePng(image, puzzleName);
    var expectedPath = toExpectedGridPath(puzzleName);
    assertThat(areFilesIdentical(outputPath, expectedPath)).isTrue();
  }

  private String toFilenameExcludingExtension(String input) {
    var path = Paths.get(input);
    return urlConverter.toFilenameExcludingExtension(path.getFileName().toString());
  }

  private String toExpectedGridPath(String puzzleName) {
    return String.format("integration-test-files/expected-grid/%s.png", puzzleName);
  }

  private boolean areFilesIdentical(String path1, String path2) {
    try {
      return Files.mismatch(Path.of(path1), Path.of(path2)) == -1;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
