package uk.co.mruoc.cws.solver.textract;

import lombok.Builder;
import lombok.ToString;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.Arrays;

@ToString
@Builder
public class Corners {

  final Point topLeft;
  final Point topRight;
  final Point bottomRight;
  final Point bottomLeft;

  public Mat perspectiveTransform() {
    return perspectiveTransform(1);
  }

  public Mat perspectiveTransform(double scale) {
    var srcPoints = new MatOfPoint2f(toArray(scale));
    var dstPoints = toDestinationPoints(maxSize());
    return Imgproc.getPerspectiveTransform(srcPoints, dstPoints);
  }

  public Size padedMaxSize(double paddingPercentage) {
    var maxSize = maxSize();
    var paddedWidth = pad(maxSize.width, paddingPercentage);
    var paddedHeight = pad(maxSize.height, paddingPercentage);
    return new Size(paddedWidth, paddedHeight);
  }

  private double pad(double length, double padPercentage) {
    return length + (padPercentage * length);
  }

  public Size maxSize() {
    var maxWidth = maxWidth();
    var maxHeight = maxHeight();
    return new Size(maxWidth, maxHeight);
  }

  public Point center() {
    var x = (topLeft.x + topRight.x + bottomRight.x + bottomLeft.x) / 4.0;
    var y = (topLeft.y + topRight.y + bottomRight.y + bottomLeft.y) / 4.0;
    return new Point(x, y);
  }

  private Point expand(Point p, Point center, double scale) {
    var x = center.x + (p.x - center.x) * scale;
    var y = center.y + (p.y - center.y) * scale;
    return new Point(x, y);
  }

  public void drawOnto(Mat debug) {
    int radius = 10;
    var color = new Scalar(0, 0, 255);
    int thickness = 5;
    Imgproc.circle(debug, topLeft, radius, color, thickness);
    Imgproc.circle(debug, topRight, radius, color, thickness);
    Imgproc.circle(debug, bottomLeft, radius, color, thickness);
    Imgproc.circle(debug, bottomRight, radius, color, thickness);
  }

  private Point[] toArray(double scale) {
    if (scale == 1d) {
      return new Point[]{topLeft, topRight, bottomRight, bottomLeft};
    }
    var center = center();
    return new Point[]{
            expand(topLeft, center, scale),
            expand(topRight, center, scale),
            expand(bottomRight, center, scale),
            expand(bottomLeft, center, scale)
    };
  }

  public Corners expand(double scale) {
    var center = center();
    return Corners.builder()
            .topLeft(expand(topLeft, center, scale))
            .topRight(expand(topLeft, center, scale))
            .bottomRight(expand(bottomRight, center, scale))
            .bottomLeft(expand(bottomLeft, center, scale))
            .build();
  }

  private MatOfPoint2f toDestinationPoints(Size maxSize) {
    return new MatOfPoint2f(
        new Point(0, 0),
        new Point(maxSize.width - 1, 0),
        new Point(maxSize.width - 1, maxSize.height - 1),
        new Point(0, maxSize.height - 1));
  }

  private int maxWidth() {
    double bottomWidth = distance(bottomRight, bottomLeft);
    double topWidth = distance(topRight, topLeft);
    return (int) Math.max(bottomWidth, topWidth);
  }

  private int maxHeight() {
    double leftHeight = distance(topLeft, bottomLeft);
    double rightHeight = distance(topRight, bottomRight);
    return (int) Math.max(leftHeight, rightHeight);
  }

  private double distance(Point p1, Point p2) {
    double dx = p2.x - p1.x;
    double dy = p2.y - p1.y;
    return Math.sqrt(dx * dx + dy * dy);
  }
}
