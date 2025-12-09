package uk.co.mruoc.cws.image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

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

  public byte[] toPngBytes(Mat input) {
    var output = new MatOfByte();
    Imgcodecs.imencode(".png", input, output);
    return output.toArray();
  }
}
