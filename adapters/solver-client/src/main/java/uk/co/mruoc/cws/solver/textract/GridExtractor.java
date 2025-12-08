package uk.co.mruoc.cws.solver.textract;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import uk.co.mruoc.cws.image.ImageConverter;

@RequiredArgsConstructor
@Slf4j
public class GridExtractor {

  private final ImageConverter imageConverter;

  public GridExtractor() {
    this(new ImageConverter());
  }

  public Mat extractGrid(BufferedImage image) {
    return extractGrid(imageConverter.toBytes(image));
  }

  private Mat extractGrid(byte[] bytes) {
    var original = toOriginal(bytes);
    return extractGrid(original);
  }

  private Mat toOriginal(byte[] bytes) {
    var original = Imgcodecs.imdecode(new MatOfByte(bytes), Imgcodecs.IMREAD_COLOR);
    Imgcodecs.imwrite("1-original.png", original);
    return original;
  }

  public Mat extractGrid(Mat input) {
    // 1. Preprocess
    Mat gray = new Mat();
    Imgproc.cvtColor(input, gray, Imgproc.COLOR_BGR2GRAY);

    Mat bw = new Mat();
    Imgproc.adaptiveThreshold(
        gray, bw, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 51, 5);
    Imgcodecs.imwrite("2-grid-extractor-binary.png", bw);

    List<MatOfPoint> contours = new ArrayList<>();
    Imgproc.findContours(
        bw, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

    if (contours.isEmpty()) {
      throw new RuntimeException("No grid contours found!");
    }

    MatOfPoint largest = contours.getFirst();
    double maxArea = 0;
    for (MatOfPoint c : contours) {
      double area = Imgproc.contourArea(c);
      if (area > maxArea) {
        maxArea = area;
        largest = c;
      }
    }

    MatOfPoint2f contour2f = new MatOfPoint2f(largest.toArray());
    RotatedRect rect = Imgproc.minAreaRect(contour2f);

    Point[] corners1 = new Point[4];
    rect.points(corners1);
    var debug = new Mat(input.size(), input.type(), new Scalar(255, 255, 255));
    Point[] vertices = new Point[4];
    rect.points(vertices);
    for (int i = 0; i < 4; i++) {
      Imgproc.line(debug, vertices[i], vertices[(i + 1) % 4], new Scalar(0, 0, 255), 2);
    }
    Imgproc.drawContours(debug, List.of(largest), -1, new Scalar(255, 0, 0), 2);
    Imgcodecs.imwrite("2-grid-extractor-contours.jpg", debug);

    // 6. Get corners and order them: top-left, top-right, bottom-right, bottom-left
    Point[] corners = new Point[4];
    rect.points(corners);
    Point[] ordered = orderCorners(corners);

    // 7. Compute width and height of the new rectangle
    double bottomWidth = distance(ordered[2], ordered[3]);
    double topWidth = distance(ordered[1], ordered[0]);
    int maxWidth = (int) Math.max(bottomWidth, topWidth);
    log.debug("bottomWidth {} topWidth {} maxWidth {}", bottomWidth, topWidth, maxWidth);

    double leftHeight = distance(ordered[1], ordered[2]);
    double rightHeight = distance(ordered[0], ordered[3]);
    int maxHeight = (int) Math.max(leftHeight, rightHeight);
    log.debug("leftHeight {} rightHeight {} maxHeight {}", leftHeight, rightHeight, maxHeight);

    MatOfPoint2f srcPoints = new MatOfPoint2f(ordered);
    MatOfPoint2f dstPoints =
        new MatOfPoint2f(
            new Point(0, 0),
            new Point(maxWidth - 1, 0),
            new Point(maxWidth - 1, maxHeight - 1),
            new Point(0, maxHeight - 1));

    Mat transform = Imgproc.getPerspectiveTransform(srcPoints, dstPoints);
    Mat warped = new Mat();
    Imgproc.warpPerspective(input, warped, transform, new Size(maxWidth, maxHeight));
    Imgcodecs.imwrite("2-grid-extractor-warped.png", warped);
    return warped;
  }

  // Orders the 4 points in TL, TR, BR, BL order
  private Point[] orderCorners(Point[] pts) {
    log.debug("ordering corners {}", Arrays.toString(pts));
    Point[] ordered = new Point[4];
    // Sum and difference of x+y and y-x to find corners
    double sumMin = Double.MAX_VALUE, sumMax = -Double.MAX_VALUE;
    double diffMin = Double.MAX_VALUE, diffMax = -Double.MAX_VALUE;
    for (Point p : pts) {
      double sum = p.x + p.y;
      double diff = p.y - p.x;
      if (sum < sumMin) {
        sumMin = sum;
        ordered[0] = p;
      } // top-left
      if (sum > sumMax) {
        sumMax = sum;
        ordered[2] = p;
      } // bottom-right
      if (diff < diffMin) {
        diffMin = diff;
        ordered[1] = p;
      } // top-right
      if (diff > diffMax) {
        diffMax = diff;
        ordered[3] = p;
      } // bottom-left
    }
    log.debug("topLeft {}", ordered[0]);
    log.debug("topRight {}", ordered[1]);
    log.debug("bottomRight {}", ordered[2]);
    log.debug("bottomLeft {}", ordered[3]);
    return ordered;
  }

  private double distance(Point p1, Point p2) {
    double dx = p2.x - p1.x;
    double dy = p2.y - p1.y;
    return Math.sqrt(dx * dx + dy * dy);
  }
}
