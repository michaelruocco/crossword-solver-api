package uk.co.mruoc.cws.solver.tesseract;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.co.mruoc.cws.image.ImageLoader.loadImage;

import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.mruoc.cws.entity.Coordinates;
import uk.co.mruoc.cws.image.ImageConverter;

public class CellFactoryIT {

  private final CellFactory processor = new CellFactory();

  private final ImageConverter imageConverter = new ImageConverter();
  private final MatConverter matConverter = new MatConverter();

  @ParameterizedTest
  @ValueSource(
      strings = {
        "/examples/puzzle1/cells/cell-22.png",
        "/examples/puzzle1/cells/cell-26.png",
        "/examples/puzzle2/cells/cell-6.png",
        "/examples/puzzle2/cells/cell-11.png",
        "/examples/puzzle2/cells/cell-22.png",
        "/examples/puzzle2/cells/cell-27.png",
        "/examples/puzzle3/cells/cell-22.png",
        "/examples/puzzle4/cells/cell-11.png",
        "/examples/puzzle5/cells/cell-22.png",
        "/examples/puzzle9/cells/cell-6.png",
        "/examples/puzzle14/cells/cell-6.png",
        "/examples/puzzle24/cells/cell-11.png",
      })
  void shouldProcessCell(String inputPath) {
    var image = loadImage(inputPath);
    var bytes = imageConverter.toBytes(image);
    var input = matConverter.toMat(bytes);

    var cell = processor.toCell(input, 1, 2);

    assertThat(cell.black()).isFalse();
    assertThat(cell.hasId()).isTrue();
    assertThat(cell.forceGetId()).matches(id -> List.of(6, 11, 22, 26, 27).contains(id));
    assertThat(cell.coordinates()).usingRecursiveComparison().isEqualTo(new Coordinates(1, 2));
  }
}
