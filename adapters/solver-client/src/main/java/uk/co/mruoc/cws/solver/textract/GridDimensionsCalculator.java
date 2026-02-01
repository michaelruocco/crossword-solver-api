package uk.co.mruoc.cws.solver.textract;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import uk.co.mruoc.cws.image.ImageConverter;
import uk.co.mruoc.cws.usecase.Image;

@RequiredArgsConstructor
@Slf4j
public class GridDimensionsCalculator {

  static {
    OpenCvInitializer.init();
  }

  private final ImageConverter imageConverter;
  private final GridExtractor gridExtractor;
  private final MatConverter matConverter;

  public GridDimensionsCalculator() {
    this(new ImageConverter(), new GridExtractor(), new MatConverter());
  }

  public GridDimensions calculateDimensions(BufferedImage image) {
    var bytes = imageConverter.toBytes(image);
    return calculateDimensions(bytes);
  }

  public GridDimensions calculateDimensions(Image image) {
    return calculateDimensions(image.getBytes());
  }

  private GridDimensions calculateDimensions(byte[] bytes) {
    var original = matConverter.toMat(bytes);
    var grid = gridExtractor.extractGrid(original);
    return calculateDimensions(grid);
  }

  public GridDimensions calculateDimensions(Mat input) {
    var grayCropped = matConverter.toGrayscale(input);
    var blurred = matConverter.blur(grayCropped);
    var binary = matConverter.toBinary(blurred);
    var smoothed = matConverter.smooth(binary);
    //min area 35 to preserve all numbers if needed
    var cleaned = matConverter.removeNoiseSmallerThan(smoothed, 1500);

    var horizontal = matConverter.toHorizontalGridLines(cleaned);
    var horizontalCoordinates = toHorizontalCoordinates(horizontal);
    var vertical = matConverter.toVerticalGridLines(cleaned);
    var verticalCoordinates = toVerticalCoordinates(vertical);
    var gridLines = matConverter.combine(horizontal, vertical);
    MatLogger.debug(gridLines, "grid-lines");

    var intersections = matConverter.toIntersections(cleaned);
    MatLogger.debug(intersections, "intersections");

    var points = matConverter.toPoints(intersections).stream()
            .sorted(Comparator.comparingDouble((Point p) -> p.y).reversed())
            .toList();

    int rowCount = horizontalCoordinates.size();
    int columnCount = verticalCoordinates.size();

    var rows = new ArrayList<List<Point>>();
    Mat out = input.clone();
    for (int y = 0; y < rowCount; y++) {
      int start = y * columnCount;
      int end = start + columnCount;
      rows.add(points.subList(start, end).stream().sorted(Comparator.comparingDouble((Point p) -> p.x)).toList());
    }
    for (var row : rows) {
      for (var p : row) {
        Imgproc.circle(out, p, 5, new Scalar(0, 0, 255), 5);
      }
    }
    MatLogger.debug(out, "grid-with-intersections");
    return GridDimensions.builder()
        .rows(horizontalCoordinates)
        .columns(verticalCoordinates)
        .points(rows)
        .build();
  }

  private List<Integer> toHorizontalCoordinates(Mat input) {
    var coordinates = toCoordinates(input, (rect) -> rect.y);
    return cluster(input, coordinates);
  }

  private List<Integer> toVerticalCoordinates(Mat input) {
    var coordinates = toCoordinates(input, (rect) -> rect.x);
    return cluster(input, coordinates);
  }

  private List<Integer> toCoordinates(Mat lines, Function<Rect, Integer> function) {
    return matConverter.toContours(lines).stream()
        .map(Imgproc::boundingRect)
        .map(function)
        .sorted()
        .toList();
  }

  private List<Integer> cluster(Mat lines, List<Integer> coordinates) {
    List<Integer> clustered = new ArrayList<>();
    int last = coordinates.getFirst();
    clustered.add(last);
    int tolerance = Math.min(lines.width(), lines.height()) / 50;
    for (int i = 1; i < coordinates.size(); i++) {
      int c = coordinates.get(i);
      if (Math.abs(c - last) > tolerance) {
        clustered.add(c);
        last = c;
      }
    }
    return clustered;
  }
}
