package uk.co.mruoc.cws.solver.textract;

import static org.opencv.imgproc.Imgproc.MORPH_RECT;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import uk.co.mruoc.cws.image.ImageConverter;

@RequiredArgsConstructor
@Slf4j
public class GridDimensionsCalculator {

  private final ImageConverter imageConverter;
  private final GridExtractor gridExtractor;

  static {
    OpenCvInitializer.init();
  }

  public GridDimensionsCalculator() {
    this(new ImageConverter(), new GridExtractor());
  }

  public GridDimensions calculateDimensions(BufferedImage image) {
    var binary = process(image);
    return calculateDimensions(binary);
  }

  private Mat process(BufferedImage image) {
    return process(imageConverter.toBytes(image));
  }

  private Mat process(byte[] bytes) {
    var grid = extractGrid(bytes);
    var gray = toGrayscale(grid);
    return toBinary(gray);
  }

  private Mat extractGrid(byte[] bytes) {
    var original = toOriginal(bytes);
    return gridExtractor.extractGrid(original);
  }

  private Mat toOriginal(byte[] bytes) {
    var original = Imgcodecs.imdecode(new MatOfByte(bytes), Imgcodecs.IMREAD_COLOR);
    Imgcodecs.imwrite("1-original.png", original);
    return original;
  }

  private Mat toGrayscale(Mat input) {
    Mat gray = new Mat();
    Imgproc.cvtColor(input, gray, Imgproc.COLOR_BGR2GRAY);
    Imgcodecs.imwrite("3-gray.png", gray);
    return gray;
  }

  private Mat toBinary(Mat input) {
    Mat binary = new Mat();
    Imgproc.adaptiveThreshold(
        input, binary, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 51, 10);
    Imgcodecs.imwrite("4-binary.png", binary);
    return binary;
  }

  private GridDimensions calculateDimensions(Mat grid) {
    var cleaned = clean(grid);
    var horizontalLines = toHorizontalGridLines(cleaned);
    var verticalLines = toVerticalGridLines(cleaned);
    var gridLines = combined(horizontalLines, verticalLines);
    Imgcodecs.imwrite("5-grid-dimensions-calculator-lines.png", gridLines);
    Imgcodecs.imwrite("5-grid-dimensions-calculator-overlayed.png", combined(grid, gridLines));

    int[][] lines = detectGridLines(horizontalLines, verticalLines);
    List<Integer> rows = Arrays.stream(lines[0]).boxed().toList();
    log.debug("rows {}", rows);
    List<Integer> columns = Arrays.stream(lines[1]).boxed().toList();
    log.debug("columns {}", columns);
    return GridDimensions.builder().columns(columns).rows(rows).build();
  }

  private int[][] detectGridLines(Mat horizontalLines, Mat verticalLines) {
    // Find line coordinates
    List<Integer> rows = extractLineCoordinates(horizontalLines, true);
    List<Integer> columns = extractLineCoordinates(verticalLines, false);

    return new int[][] {
      rows.stream().mapToInt(i -> i).toArray(), columns.stream().mapToInt(i -> i).toArray()
    };
  }

  private Mat combined(Mat m1, Mat m2) {
    Mat combined = new Mat();
    Core.bitwise_or(m1, m2, combined);
    return combined;
  }

  private Mat clean(Mat input) {
    Mat cleaned = new Mat();
    Mat openKernel = Imgproc.getStructuringElement(MORPH_RECT, new Size(5, 5));
    Imgproc.morphologyEx(input, cleaned, Imgproc.MORPH_OPEN, openKernel);
    Imgcodecs.imwrite("5a-1-cleaned.png", cleaned);
    return cleaned;
  }

  private Mat toHorizontalGridLines(Mat input) {
    Mat binary = new Mat();
    input.convertTo(binary, CvType.CV_8UC1);

    Mat horizontalsOnly = new Mat();
    int horizontalSize = Math.max(20, binary.cols() / 40);
    Mat horizontalKernel = Imgproc.getStructuringElement(MORPH_RECT, new Size(horizontalSize, 1));
    Imgproc.erode(binary, horizontalsOnly, horizontalKernel);
    Imgcodecs.imwrite("5a-2-horizontals-only.png", horizontalsOnly);

    Mat connected = new Mat();
    Mat connectKernel = Imgproc.getStructuringElement(MORPH_RECT, new Size(350, 1));
    Imgproc.dilate(horizontalsOnly, connected, connectKernel);
    Imgcodecs.imwrite("5a-3-connected.png", connected);

    Mat smallBridged = new Mat();
    Mat smallBridge = Imgproc.getStructuringElement(MORPH_RECT, new Size(1, 17));
    Imgproc.dilate(connected, smallBridged, smallBridge);
    Imgcodecs.imwrite("5a-4-small-bridged.png", smallBridged);

    Mat thinned = new Mat();
    Mat thinKernel = Imgproc.getStructuringElement(MORPH_RECT, new Size(1, 17));
    Imgproc.erode(smallBridged, thinned, thinKernel);
    Imgcodecs.imwrite("5a-5-thinned.png", thinned);

    return thinned;
  }

  private Mat toVerticalGridLines(Mat grid) {
    Mat binary = new Mat();
    grid.convertTo(binary, CvType.CV_8UC1);

    Mat verticalsOnly = new Mat();
    int verticalSize = Math.max(20, binary.rows() / 40);
    Mat verticalKernel = Imgproc.getStructuringElement(MORPH_RECT, new Size(1, verticalSize));
    Imgproc.erode(binary, verticalsOnly, verticalKernel);
    Imgcodecs.imwrite("5a-6-verticals-only.png", verticalsOnly);

    Mat connected = new Mat();
    Mat connectKernel = Imgproc.getStructuringElement(MORPH_RECT, new Size(1, 475));
    Imgproc.dilate(verticalsOnly, connected, connectKernel);
    Imgcodecs.imwrite("5a-7-connected.png", connected);

    Mat smallBridged = new Mat();
    Mat smallBridge = Imgproc.getStructuringElement(MORPH_RECT, new Size(17, 1));
    Imgproc.dilate(connected, smallBridged, smallBridge);
    Imgcodecs.imwrite("5a-8-small-bridged.png", smallBridged);

    Mat thinned = new Mat();
    Mat thinKernel = Imgproc.getStructuringElement(MORPH_RECT, new Size(17, 1));
    Imgproc.erode(smallBridged, thinned, thinKernel);
    Imgcodecs.imwrite("5a-9-thinned.png", thinned);

    return thinned;
  }

  private List<Integer> extractLineCoordinates(Mat lines, boolean horizontal) {
    if (lines == null || lines.empty()) {
      throw new IllegalArgumentException("Input image is empty");
    }

    List<Integer> coords = new ArrayList<>();
    List<MatOfPoint> contours = new ArrayList<>();
    Imgproc.findContours(
        lines, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

    for (MatOfPoint c : contours) {
      Rect r = Imgproc.boundingRect(c);
      coords.add(horizontal ? r.y : r.x);
    }

    Collections.sort(coords);
    List<Integer> clustered = new ArrayList<>();
    int last = coords.getFirst();
    clustered.add(last);

    int tolerance = Math.min(lines.width(), lines.height()) / 50;
    log.debug("tolerance {} width {} height {}", tolerance, lines.width(), lines.height());
    for (int i = 1; i < coords.size(); i++) {
      int c = coords.get(i);
      if (Math.abs(c - last) > tolerance) {
        clustered.add(c);
        last = c;
      }
    }
    log.debug("Raw count {}", coords.size());
    log.debug("Clustered count {}", clustered.size());
    return clustered;
  }
}
