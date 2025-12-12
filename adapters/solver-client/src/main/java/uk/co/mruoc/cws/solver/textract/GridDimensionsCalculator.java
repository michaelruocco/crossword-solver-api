package uk.co.mruoc.cws.solver.textract;

import static org.opencv.imgproc.Imgproc.MORPH_RECT;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import uk.co.mruoc.cws.image.ImageConverter;

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
    var binary = process(bytes);
    return calculateDimensions(binary);
  }

  private Mat process(byte[] bytes) {
    var original = matConverter.toMat(bytes);
    var grid = gridExtractor.extractGrid(original);
    var gray = matConverter.toGrayscale(grid);
    return matConverter.toBinary(gray);
  }

  private GridDimensions calculateDimensions(Mat grid) {
    var cleaned = matConverter.clean(grid);
    return GridDimensions.builder()
        .rows(toHorizontalCoordinates(cleaned))
        .columns(toVerticalCoordinates(cleaned))
        .build();
  }

  private List<Integer> toHorizontalCoordinates(Mat input) {
    var lines = toHorizontalGridLines(input);
    var coordinates = toCoordinates(lines, (rect) -> rect.y);
    return cluster(lines, coordinates);
  }

  private Mat toHorizontalGridLines(Mat input) {
    var horizontals = new Mat();
    int width = Math.max(20, input.cols() / 40);
    Imgproc.erode(input, horizontals, toKernel(width, 1));

    var connected = new Mat();
    Imgproc.dilate(horizontals, connected, toKernel(350, 1));

    var bridged = new Mat();
    Imgproc.dilate(connected, bridged, toKernel(1, 17));

    var thinned = new Mat();
    Imgproc.erode(bridged, thinned, toKernel(1, 17));
    MatLogger.debug(thinned, "horizontal-grid-lines");
    return thinned;
  }

  private List<Integer> toVerticalCoordinates(Mat input) {
    var lines = toVerticalGridLines(input);
    var coordinates = toCoordinates(lines, (rect) -> rect.x);
    return cluster(lines, coordinates);
  }

  private Mat toKernel(int width, int height) {
    return Imgproc.getStructuringElement(MORPH_RECT, new Size(width, height));
  }

  private Mat toVerticalGridLines(Mat input) {
    var verticals = new Mat();
    int height = Math.max(20, input.rows() / 40);
    Imgproc.erode(input, verticals, toKernel(1, height));

    var connected = new Mat();
    Imgproc.dilate(verticals, connected, toKernel(1, 475));

    var bridged = new Mat();
    Imgproc.dilate(connected, bridged, toKernel(17, 1));

    var thinned = new Mat();
    Imgproc.erode(bridged, thinned, toKernel(17, 1));
    MatLogger.debug(thinned, "vertical-grid-lines");
    return thinned;
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
