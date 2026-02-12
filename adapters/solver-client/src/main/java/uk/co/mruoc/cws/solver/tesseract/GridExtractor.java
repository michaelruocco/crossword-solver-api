package uk.co.mruoc.cws.solver.tesseract;

import java.awt.image.BufferedImage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Mat;
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
  }
}
