package uk.co.mruoc.cws.solver.textract;

import static org.opencv.imgproc.Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C;
import static org.opencv.imgproc.Imgproc.CHAIN_APPROX_SIMPLE;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.COLOR_GRAY2BGR;
import static org.opencv.imgproc.Imgproc.INTER_NEAREST;
import static org.opencv.imgproc.Imgproc.RETR_EXTERNAL;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY_INV;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.imageio.ImageIO;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import uk.co.mruoc.cws.image.ImageException;

public class MatConverter {

  static {
    OpenCvInitializer.init();
  }

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

  public byte[] toPngBytes(Mat input) {
    var output = new MatOfByte();
    Imgcodecs.imencode(".png", input, output);
    return output.toArray();
  }

  public Mat toBinary(Mat input) {
    var binary = new Mat();
    Imgproc.adaptiveThreshold(
        input, binary, 255, ADAPTIVE_THRESH_GAUSSIAN_C, THRESH_BINARY_INV, 51, 10);
    return binary;
  }

  public Mat toGrayscale(Mat input) {
    var gray = new Mat();
    Imgproc.cvtColor(input, gray, COLOR_BGR2GRAY);
    return gray;
  }

  public Collection<MatOfPoint> toContours(Mat binary) {
    List<MatOfPoint> contours = new ArrayList<>();
    Imgproc.findContours(binary, contours, new Mat(), RETR_EXTERNAL, CHAIN_APPROX_SIMPLE);
    return contours;
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
}
