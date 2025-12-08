package uk.co.mruoc.cws.solver.textract;

import java.awt.image.BufferedImage;
import lombok.RequiredArgsConstructor;
import nu.pattern.OpenCV;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import uk.co.mruoc.cws.image.ImageConverter;

@RequiredArgsConstructor
public class ImageProcessor {

  private final ImageConverter imageConverter;
  private final GridExtractor gridExtractor;

  public ImageProcessor() {
    this(new ImageConverter(), new GridExtractor());
    OpenCV.loadLocally();
  }

  public Mat process(BufferedImage image) {
    return process(imageConverter.toBytes(image));
  }

  public Mat extractGrid(BufferedImage image) {
    return extractGrid(imageConverter.toBytes(image));
  }

  private Mat extractGrid(byte[] bytes) {
    var original = toOriginal(bytes);
    return gridExtractor.extractGrid(original);
  }

  private Mat process(byte[] bytes) {
    var grid = extractGrid(bytes);
    var gray = toGrayscale(grid);
    return toBinary(gray);
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
}
