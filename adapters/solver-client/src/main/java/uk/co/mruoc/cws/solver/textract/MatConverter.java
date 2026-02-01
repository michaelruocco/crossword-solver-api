package uk.co.mruoc.cws.solver.textract;

import static org.opencv.imgproc.Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C;
import static org.opencv.imgproc.Imgproc.CHAIN_APPROX_SIMPLE;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.COLOR_GRAY2BGR;
import static org.opencv.imgproc.Imgproc.INTER_NEAREST;
import static org.opencv.imgproc.Imgproc.MORPH_OPEN;
import static org.opencv.imgproc.Imgproc.MORPH_RECT;
import static org.opencv.imgproc.Imgproc.RETR_EXTERNAL;
import static org.opencv.imgproc.Imgproc.RETR_LIST;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY_INV;
import static uk.co.mruoc.cws.solver.textract.RectUtils.contains;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.imageio.ImageIO;

import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import uk.co.mruoc.cws.image.ImageException;

@Slf4j
public class MatConverter {

  public BufferedImage toBufferedImage(Mat input) {
    byte[] bytes = toPngBytes(input);
    return toBufferedImage(bytes);
  }

  public BufferedImage toBufferedImage(byte[] bytes) {
    try {
      return ImageIO.read(new ByteArrayInputStream(bytes));
    } catch (IOException e) {
      throw new ImageException(e);
    }
  }

  public Mat toMat(byte[] bytes) {
    return Imgcodecs.imdecode(new MatOfByte(bytes), Imgcodecs.IMREAD_COLOR);
  }

  public byte[] toPngBytes(Mat input) {
    var output = new MatOfByte();
    Imgcodecs.imencode(".png", input, output);
    return output.toArray();
  }

  public Mat wrapGrid(Mat input) {
    var gray = toGrayscale(input);
    var binary = toBinary(gray, 5);
    var maxContour = toLargestContour(binary);

    var peri = Imgproc.arcLength(maxContour, true);
    var approx = new MatOfPoint2f();
    Imgproc.approxPolyDP(maxContour, approx, 0.02 * peri, true);
    var srcPts = approx.toArray();
    var outerCorners = orderCorners(srcPts);
    var cornersDebug = input.clone();
    outerCorners.drawOnto(cornersDebug);
    MatLogger.debug(cornersDebug, "outer-corners");

    var M = outerCorners.perspectiveTransform(1.15);
    var warped = new Mat();
    Imgproc.warpPerspective(input, warped, M, outerCorners.maxSize());

    /*var outerCorners = orderCorners(maxContour.toArray());
    var cornersDebug = input.clone();
    outerCorners.drawOnto(cornersDebug);
    MatLogger.debug(cornersDebug, "outer-corners");
    var M = outerCorners.perspectiveTransform(1.15);
    var warped = new Mat();
    Imgproc.warpPerspective(input, warped, M, outerCorners.maxSize());*/
    MatLogger.debug(warped, "warped");
    return warped;
  }

  public Mat crop(Mat input, int expandPixels) {
    var gray = toGrayscale(input);
    var binary = toBinary(gray, 5);
    var maxContour = toLargestContour(binary);
    var boundingRect = Imgproc.boundingRect(maxContour);

    int newX = Math.max(boundingRect.x - expandPixels, 0);
    int newY = Math.max(boundingRect.y - expandPixels, 0);
    int newWidth = Math.min(boundingRect.width + 2 * expandPixels,  input.cols() - newX);
    int newHeight = Math.min(boundingRect.height + 2 * expandPixels, input.rows() - newY);
    var expandedRect = new Rect(newX, newY, newWidth, newHeight);
    var cropped = new Mat(input, expandedRect);
    MatLogger.debug(cropped, "cropped");
    return cropped;
  }

  public Mat toIntersections(Mat input) {
    Mat horizontal = toHorizontalGridLines(input);
    Mat vertical = toVerticalGridLines(input);
    return intersect(horizontal, vertical);
  }

  public Mat intersect(Mat input1, Mat input2) {
    Mat intersections = new Mat();
    Core.bitwise_and(input1, input2, intersections);
    return intersections;
  }

  public Mat combine(Mat input1, Mat input2) {
    Mat output = new Mat();
    Core.bitwise_or(input1, input2, output);
    return output;
  }

  public Mat toHorizontalGridLines(Mat input) {
    var horizontals = new Mat();
    int width = Math.max(20, input.cols() / 40);
    Imgproc.erode(input, horizontals, toKernel(width, 1));

    var connected = new Mat();
    Imgproc.dilate(horizontals, connected, toKernel(350, 1));

    var bridged = new Mat();
    Imgproc.dilate(connected, bridged, toKernel(1, 17));

    var thinned = new Mat();
    Imgproc.erode(bridged, thinned, toKernel(1, 17));
    //MatLogger.debug(thinned, "horizontal-grid-lines");
    return thinned;
  }

  public Mat toVerticalGridLines(Mat input) {
    var verticals = new Mat();
    int height = Math.max(20, input.rows() / 40);
    Imgproc.erode(input, verticals, toKernel(1, height));

    var connected = new Mat();
    Imgproc.dilate(verticals, connected, toKernel(1, 475));

    var bridged = new Mat();
    Imgproc.dilate(connected, bridged, toKernel(17, 1));

    var thinned = new Mat();
    Imgproc.erode(bridged, thinned, toKernel(17, 1));
    //MatLogger.debug(thinned, "vertical-grid-lines");
    return thinned;
  }

  public Corners toCornersOfLargestContour(Mat input) {
    var gray = toGrayscale(input);
    var binary = toBinary(gray, 5);
    var largestContour = toLargestContour(binary);
    return toCorners(largestContour);
  }

  public Corners toCorners(MatOfPoint2f contour) {
    var corners = new Point[4];
    var rect = Imgproc.minAreaRect(contour);
    rect.points(corners);
    return orderCorners(corners);
  }

  public Mat toGrayscale(Mat input) {
    var gray = new Mat();
    Imgproc.cvtColor(input, gray, COLOR_BGR2GRAY);
    return gray;
  }

  public Mat toBinary(Mat input) {
    return toBinary(input, 10);
  }

  public Mat toBinary(Mat input, int c) {
    var binary = new Mat();
    Imgproc.adaptiveThreshold(
        input, binary, 255, ADAPTIVE_THRESH_GAUSSIAN_C, THRESH_BINARY_INV, 51, c);
    return binary;
  }

  public Mat blur(Mat input) {
    Mat blurred = new Mat();
    Imgproc.GaussianBlur(input, blurred, new Size(7,7), 0);
    MatLogger.debug(blurred, "blurred");
    return blurred;
  }

  public Mat smooth(Mat input) {
    var smoothed = new Mat();
    Imgproc.morphologyEx(input, smoothed, MORPH_OPEN, toKernel(3));
    MatLogger.debug(smoothed, "smoothed");
    return smoothed;
  }

  public Mat removeNoiseSmallerThan(Mat input, int minArea) {
    var cleaned = input.clone();

    var gridBounds = Imgproc.boundingRect(toLargestContour(cleaned));
    var contours = toContours(input, RETR_LIST);
    var contoursToFill = contours.stream()
            .filter(contour -> outsideBounds(contour, gridBounds) || hasAreaLessThanOrEqualTo(contour, minArea))
            .toList();
    fillBlack(cleaned, contoursToFill);
    log.info("filled {} of {} contours", contoursToFill.size(), contours.size());
    MatLogger.debug(cleaned, "noise-removed");
    return cleaned;
  }

  private boolean outsideBounds(MatOfPoint contour, Rect gridBounds) {
    var contourBounds = Imgproc.boundingRect(contour);
    return !contains(gridBounds, contourBounds);
  }

  public Mat resize(Mat original, int width, int height) {
    Mat resized = new Mat();
    Size size = new Size(width, height);
    Imgproc.resize(original, resized, size);
    return resized;
  }

  public Mat scale(Mat input, int scale) {
    return scale(input, scale, scale);
  }

  public Mat scale(Mat input, int widthScale, int heightScale) {
    var scaled = new Mat();
    Imgproc.resize(input, scaled, new Size(), widthScale, heightScale, INTER_NEAREST);
    return scaled;
  }

  public Mat invert(Mat input) {
    var inverted = new Mat();
    Core.bitwise_not(input, inverted);
    Imgproc.cvtColor(inverted, inverted, COLOR_GRAY2BGR);
    return inverted;
  }

  public Mat clean(Mat input) {
    return clean(input, 5);
  }

  public Mat clean(Mat input, int size) {
    return clean(input, new Size(size, size));
  }

  public Mat clean(Mat input, Size kernelSize) {
    var cleaned = new Mat();
    var openKernel = Imgproc.getStructuringElement(MORPH_RECT, kernelSize);
    Imgproc.morphologyEx(input, cleaned, MORPH_OPEN, openKernel);
    return cleaned;
  }

  public MatOfPoint2f toLargestContour(Mat input) {
    var contours = toContours(input);
    var largest = new MatOfPoint2f();
    double maxArea = 0;
    for (MatOfPoint c : contours) {
      double area = Imgproc.contourArea(c);
      if (area > maxArea) {
        maxArea = area;
        largest = new MatOfPoint2f(c.toArray());
      }
    }
    return largest;
  }

  public Collection<MatOfPoint> toContours(Mat input) {
    return toContours(input, RETR_EXTERNAL);
  }

  public Collection<MatOfPoint> toContours(Mat input, int mode) {
    var contours = new ArrayList<MatOfPoint>();
    Imgproc.findContours(input, contours, new Mat(), mode, CHAIN_APPROX_SIMPLE);
    return contours;
  }

  public Corners orderCorners(Point[] points) {
    Point topLeft = null;
    Point topRight = null;
    Point bottomRight = null;
    Point bottomLeft = null;

    double sumMin = Double.MAX_VALUE;
    double sumMax = -Double.MAX_VALUE;
    double diffMin = Double.MAX_VALUE;
    double diffMax = -Double.MAX_VALUE;

    for (var point : points) {
      double sum = point.x + point.y;
      double diff = point.y - point.x;
      if (sum < sumMin) {
        sumMin = sum;
        topLeft = point;
      }
      if (sum > sumMax) {
        sumMax = sum;
        bottomRight = point;
      }
      if (diff < diffMin) {
        diffMin = diff;
        topRight = point;
      }
      if (diff > diffMax) {
        diffMax = diff;
        bottomLeft = point;
      }
    }
    return Corners.builder()
        .topLeft(topLeft)
        .topRight(topRight)
        .bottomRight(bottomRight)
        .bottomLeft(bottomLeft)
        .build();
  }

  public List<Point> toPoints(Mat input) {
    return toContours(input).stream()
            .map(this::toCenterPoint)
            .flatMap(Optional::stream)
            .toList();
  }

  private Optional<Point> toCenterPoint(MatOfPoint contour) {
    Moments m = Imgproc.moments(contour);
    if (m.m00 == 0) {
      return Optional.empty();
    }
    double cx = m.m10 / m.m00;
    double cy = m.m01 / m.m00;
    return Optional.of(new Point(cx, cy));
  }

  private boolean hasAreaLessThanOrEqualTo(MatOfPoint contour, int threshold) {
    var box = Imgproc.boundingRect(contour);
    //TODO make these configuration inputs rather than hardcoded 80
    if (box.width > 80) {
      return false;
    }
    if (box.height > 80) {
      return false;
    }
    var area = Imgproc.contourArea(contour);
    return area <= threshold;
  }

  private void fillBlack(Mat input, Collection<MatOfPoint> contours) {
    contours.forEach(contour -> fillBlack(input, contour));
  }

  private void fillBlack(Mat input, MatOfPoint contour) {
    fill(input, contour, new Scalar(0, 0, 0));
  }

  private void fill(Mat image, MatOfPoint contour, Scalar colour) {
    Imgproc.drawContours(image, List.of(contour), -1, colour, -1);
  }

  private Mat toKernel(int size) {
    return toKernel(size, size);
  }

  private Mat toKernel(int width, int height) {
    return Imgproc.getStructuringElement(MORPH_RECT, new Size(width, height));
  }
}
