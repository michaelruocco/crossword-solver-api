package uk.co.mruoc.cws.image;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;
import javax.imageio.ImageIO;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageLoader {

  public static BufferedImage loadImage(String path) {
    try (var is = ImageLoader.class.getResourceAsStream(path)) {
      if (Objects.isNull(is)) {
        throw new IOException(String.format("Image not found on classpath at %s", path));
      }
      return ImageIO.read(is);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
