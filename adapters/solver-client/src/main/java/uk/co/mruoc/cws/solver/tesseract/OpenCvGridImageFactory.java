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
import org.opencv.imgproc.Imgproc;
import uk.co.mruoc.cws.entity.Cell;
import uk.co.mruoc.cws.entity.Coordinates;
import uk.co.mruoc.cws.entity.Grid;
import uk.co.mruoc.cws.usecase.GridImageFactory;

@RequiredArgsConstructor
public class OpenCvGridImageFactory implements GridImageFactory {

  static {
    OpenCvInitializer.init();
  }

  private static final Scalar WHITE = new Scalar(255, 255, 255);
  private static final Scalar BLACK = new Scalar(0, 0, 0);

  private final MatConverter matConverter;
  private final MatConcatenator matConcatenator;
  private final int fontFace;

  public OpenCvGridImageFactory() {
    this(new MatConverter(), new MatConcatenator(), Imgproc.FONT_HERSHEY_SIMPLEX);
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
      return buildCellMat(grid, BLACK);
    }
    return buildWhiteCellMat(grid, cell);
  }

  private Mat buildWhiteCellMat(Grid grid, Cell cell) {
    var mat = buildCellMat(grid, WHITE);
    var idMat = cell.getId().map(id -> drawId(mat, id)).orElse(mat);
    return cell.getLetter().map(letter -> drawLetter(idMat, letter)).orElse(idMat);
  }

  private Mat buildCellMat(Grid grid, Scalar color) {
    var width = grid.columnWidth() * 4;
    var height = grid.rowHeight() * 4;
    var background = new Mat(width, height, CV_8UC1, color);
    var mat = new Mat();
    var borderSize = 7;
    Core.copyMakeBorder(
        background, mat, borderSize, borderSize, borderSize, borderSize, BORDER_CONSTANT, BLACK);
    return mat;
  }

  private Mat drawId(Mat mat, int id) {
    var text = Integer.toString(id);
    double fontScale = 3;
    int thickness = 15;
    var textSize = Imgproc.getTextSize(text, fontFace, fontScale, thickness, new int[1]);
    var x = mat.width() * 0.05;
    var y = (mat.height() * 0.1) + textSize.height;
    var textLocation = new Point(x, y);
    var idMat = mat.clone();
    Imgproc.putText(idMat, text, textLocation, fontFace, fontScale, BLACK, thickness);
    return idMat;
  }

  private Mat drawLetter(Mat mat, char letter) {
    var text = String.valueOf(letter);
    double fontScale = 7.0;
    int thickness = 20;
    var textSize = Imgproc.getTextSize(text, fontFace, fontScale, thickness, new int[1]);
    var x = (mat.width() - textSize.width) / 2.0;
    var y = (mat.height() + textSize.height) / 2.0;
    var letterMat = mat.clone();
    Imgproc.putText(letterMat, text, new Point(x, y), fontFace, fontScale, BLACK, thickness);
    return letterMat;
  }
}
