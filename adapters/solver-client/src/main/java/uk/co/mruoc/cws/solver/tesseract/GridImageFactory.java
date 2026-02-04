package uk.co.mruoc.cws.solver.tesseract;

import static org.opencv.core.Core.BORDER_CONSTANT;
import static org.opencv.core.CvType.CV_8UC1;

import java.awt.image.BufferedImage;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import uk.co.mruoc.cws.entity.Coordinates;
import uk.co.mruoc.cws.entity.Grid;

@RequiredArgsConstructor
public class GridImageFactory {

  static {
    OpenCvInitializer.init();
  }

  private static final Scalar WHITE = new Scalar(255, 255, 255);
  private static final Scalar BLACK = new Scalar(0, 0, 0);

  private final MatConverter matConverter;
  private final MatConcatenator matConcatenator;

  public GridImageFactory() {
    this(new MatConverter(), new MatConcatenator());
  }

  public BufferedImage toImage(Grid grid) {
    return matConverter.toBufferedImage(toMat(grid));
  }

  public Mat toMat(Grid grid) {
    var rows =
        IntStream.iterate(grid.numberOfRows(), i -> i > -1, i -> i - 1)
            .mapToObj(y -> toRow(grid, y))
            .toList();
    return matConcatenator.verticalConcat(rows);
  }

  private Mat toRow(Grid grid, int y) {
    var cells =
        IntStream.rangeClosed(0, grid.numberOfColumns())
            .mapToObj(x -> toProcessedCellMat(grid, x, y))
            .toList();
    return matConcatenator.horizontalConcat(cells);
  }

  private Mat toProcessedCellMat(Grid grid, int x, int y) {
    var cell = grid.findByCoordinates(new Coordinates(x, y));
    if (cell.black()) {
      return buildCell(grid, BLACK);
    }
    return cell.getId().map(id -> buildCell(grid, id)).orElseGet(() -> buildCell(grid, WHITE));
  }

  private Mat buildCell(Grid grid, Scalar color) {
    var width = grid.columnWidth() * 4;
    var height = grid.rowHeight() * 4;
    var background = new Mat(width, height, CV_8UC1, color);
    var mat = new Mat();
    var borderSize = 7;
    Core.copyMakeBorder(
        background, mat, borderSize, borderSize, borderSize, borderSize, BORDER_CONSTANT, BLACK);
    return mat;
  }

  private Mat buildCell(Grid grid, Integer id) {
    var cell = buildCell(grid, WHITE);
    var text = Integer.toString(id);
    int fontFace = Imgproc.FONT_HERSHEY_SIMPLEX;
    double fontScale = 5.0;
    int thickness = 20;
    Size textSize = Imgproc.getTextSize(text, fontFace, fontScale, thickness, new int[1]);
    var textX = cell.width() * 0.05;
    var textY = (cell.height() * 0.1) + textSize.height;
    var textLocation = new Point(textX, textY);
    Imgproc.putText(cell, text, textLocation, fontFace, fontScale, BLACK, thickness);
    return cell;
  }
}
