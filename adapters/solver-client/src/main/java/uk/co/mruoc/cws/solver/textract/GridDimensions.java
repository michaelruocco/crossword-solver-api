package uk.co.mruoc.cws.solver.textract;

import static org.opencv.core.Core.BORDER_REPLICATE;
import static org.opencv.imgproc.Imgproc.INTER_LINEAR;

import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

@Builder
@Data
public class GridDimensions {

  private final List<Integer> rows;
  private final List<Integer> columns;
  private final List<List<Point>> points;

  public int getNumberOfRows() {
    return rows.size() - 1;
  }

  public int getNumberOfColumns() {
    return columns.size() - 1;
  }

  public int getAverageColumnWidth() {
    var widths = getColumnWidths();
    return toMedian(widths);
  }

  public int getAverageRowHeight() {
    var heights = getRowHeights();
    return toMedian(heights);
  }

  public Mat toCell(Mat grid, int x, int y) {
    var source = getSourcePoints(x, y);
    int width = getColumnWidth(x);
    int height = getRowHeight(y);
    var destination = getDestinationPoints(width, height);

    var transform = Imgproc.getPerspectiveTransform(source, destination);
    var cell = new Mat();
    var size = new Size(width, height);
    Imgproc.warpPerspective(grid, cell, transform, size, INTER_LINEAR, BORDER_REPLICATE);
    return cell;
  }

  private MatOfPoint2f getSourcePoints(int x, int y) {
    var bl = points.get(y).get(x);
    var br = points.get(y).get(x + 1);
    var tr = points.get(y + 1).get(x + 1);
    var tl = points.get(y + 1).get(x);
    return new MatOfPoint2f(tl, tr, br, bl);
  }

  private MatOfPoint2f getDestinationPoints(int width, int height) {
    return new MatOfPoint2f(
        new Point(0, 0),
        new Point(width - 1, 0),
        new Point(width - 1, height - 1),
        new Point(0, height - 1));
  }

  private List<Integer> getColumnWidths() {
    return IntStream.range(0, getNumberOfColumns()).mapToObj(this::getColumnWidth).toList();
  }

  private List<Integer> getRowHeights() {
    return IntStream.range(0, getNumberOfRows()).mapToObj(this::getRowHeight).toList();
  }

  private int getRowHeight(int y) {
    var top = rows.get(y);
    var bottom = rows.get(y + 1);
    return bottom - top;
  }

  private int getColumnWidth(int x) {
    var left = columns.get(x);
    var right = columns.get(x + 1);
    return right - left;
  }

  private int toMedian(Collection<Integer> values) {
    return (int) new Median().evaluate(values.stream().mapToDouble(i -> i).toArray());
  }
}
