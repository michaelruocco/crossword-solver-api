package uk.co.mruoc.cws.solver.textract;

import lombok.Builder;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

@Builder
public class Corners {

  final Point topLeft;
  final Point topRight;
  final Point bottomRight;
  final Point bottomLeft;

  public Mat perspectiveTransform() {
    var srcPoints = new MatOfPoint2f(toArray());
    var dstPoints = toDestinationPoints(maxSize());
    return Imgproc.getPerspectiveTransform(srcPoints, dstPoints);
  }

  public Size maxSize() {
    var maxWidth = maxWidth();
    var maxHeight = maxHeight();
    return new Size(maxWidth, maxHeight);
  }

  private Point[] toArray() {
    return new Point[] {topLeft, topRight, bottomRight, bottomLeft};
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
