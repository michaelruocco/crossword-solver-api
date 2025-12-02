package uk.co.mruoc.cws.usecase;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StubImageDownloader implements ImageDownloader {

  private final UrlConverter urlConverter;

  public StubImageDownloader() {
    this(new UrlConverter());
  }

  @Override
  public BufferedImage downloadImage(String imageUrl) {
    var path = toClasspathResourcePath(imageUrl);
    try (var input = StubImageDownloader.class.getResourceAsStream(path)) {
      if (Objects.isNull(input)) {
        throw new RuntimeException(String.format("file not found at path %s", path));
      }
      return ImageIO.read(input);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private String toClasspathResourcePath(String imageUrl) {
    var puzzleName = urlConverter.toFilenameExcludingExtension(imageUrl);
    var filename = urlConverter.toFilename(imageUrl);
    return String.format("/examples/%s/%s", puzzleName, filename);
  }
}
