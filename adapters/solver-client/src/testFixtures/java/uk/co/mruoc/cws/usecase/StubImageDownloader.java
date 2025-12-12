package uk.co.mruoc.cws.usecase;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
  public Image downloadImage(String imageUrl) {
    var bytes = toImageBytes(imageUrl);
    return Image.builder()
        .name(urlConverter.toFilenameExcludingExtension(imageUrl))
        .format(urlConverter.toExtension(imageUrl))
        .bufferedImage(toBufferedImage(bytes))
        .bytes(bytes)
        .hash("blah") // TODO calculate hash
        .build();
  }

  private byte[] toImageBytes(String imageUrl) {
    try (var stream = toInputStream(imageUrl)) {
      return stream.readAllBytes();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public BufferedImage toBufferedImage(byte[] bytes) {
    try (var stream = new ByteArrayInputStream(bytes)) {
      return ImageIO.read(stream);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private InputStream toInputStream(String imageUrl) {
    var path = toClasspathResourcePath(imageUrl);
    var stream = StubImageDownloader.class.getResourceAsStream(path);
    if (Objects.isNull(stream)) {
      throw new RuntimeException(String.format("file not found at path %s", path));
    }
    return stream;
  }

  private String toClasspathResourcePath(String imageUrl) {
    var puzzleName = urlConverter.toFilenameExcludingExtension(imageUrl);
    var filename = urlConverter.toFilename(imageUrl);
    return String.format("/examples/%s/%s", puzzleName, filename);
  }
}
