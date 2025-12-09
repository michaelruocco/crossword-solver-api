package uk.co.mruoc.cws.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageWriter {

  public static void writeImage(BufferedImage image, String outputPath) {
    try {
      var output = new File(outputPath);
      Files.createDirectories(Path.of(outputPath).getParent());
      ImageIO.write(image, "png", output);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
