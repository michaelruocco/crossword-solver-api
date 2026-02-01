package uk.co.mruoc.cws.solver.textract;

import java.awt.image.BufferedImage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import uk.co.mruoc.cws.image.ImageConverter;

@RequiredArgsConstructor
@Slf4j
public class GridExtractor {

  private final ImageConverter imageConverter;
  private final MatConverter matConverter;

  public GridExtractor() {
    this(new ImageConverter(), new MatConverter());
  }

  public Mat extractGrid(BufferedImage image) {
    return extractGrid(imageConverter.toBytes(image));
  }

  private Mat extractGrid(byte[] bytes) {
    var original = matConverter.toMat(bytes);
    return extractGrid(original);
  }

  public Mat extractGrid(Mat input) {
    var warped = matConverter.wrapGrid(input);
    return matConverter.crop(warped, 10);

    //var corners = matConverter.toCornersOfLargestContour(input);
    //return warpPerspective(input, corners);
  }

  /*private Mat warpPerspective(Mat input, Corners corners) {
    var transform = corners.perspectiveTransform();
    var maxSize = corners.maxSize();
    var warped = new Mat();
    Imgproc.warpPerspective(input, warped, transform, maxSize);
    return warped;
  }*/
}
