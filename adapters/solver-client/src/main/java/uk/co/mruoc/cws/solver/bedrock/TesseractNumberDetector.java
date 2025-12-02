package uk.co.mruoc.cws.solver.bedrock;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Optional;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

@RequiredArgsConstructor
public class TesseractNumberDetector {

  private final Tesseract tesseract;

  public Optional<Integer> toNumberIfPresent(Mat cell) {
    BufferedImage image = toBufferedImage(cell);
    saveImage(image, "8-temp.png", "png");
    try {
      String result = tesseract.doOCR(image);
      result = result.replaceAll("\\s+", "");
      if (result.matches(".*\\d.*")) {
        return Optional.of(Integer.parseInt(result));
      }
      return Optional.empty();
    } catch (TesseractException e) {
      throw new RuntimeException(e);
    }
  }

  private BufferedImage toBufferedImage(Mat mat) {
    Mat tmp = new Mat();
    if (mat.channels() == 1) {
      Imgproc.cvtColor(mat, tmp, Imgproc.COLOR_GRAY2BGR);
    } else {
      tmp = mat;
    }

    MatOfByte mob = new MatOfByte();
    Imgcodecs.imencode(".png", tmp, mob);
    byte[] byteArray = mob.toArray();

    try {
      return ImageIO.read(new ByteArrayInputStream(byteArray));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void saveImage(BufferedImage image, String filePath, String format) {
    try {
      ImageIO.write(image, format, new File(filePath));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
