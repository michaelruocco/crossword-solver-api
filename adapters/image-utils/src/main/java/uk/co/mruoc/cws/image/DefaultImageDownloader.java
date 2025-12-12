package uk.co.mruoc.cws.image;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import uk.co.mruoc.cws.usecase.Image;
import uk.co.mruoc.cws.usecase.ImageDownloader;
import uk.co.mruoc.cws.usecase.UrlConverter;

@RequiredArgsConstructor
public class DefaultImageDownloader implements ImageDownloader {

  private final ImageRotator rotator;
  private final UrlConverter urlConverter;
  private final ImageConverter imageConverter;

  public DefaultImageDownloader() {
    this(new ImageRotator(), new UrlConverter(), new ImageConverter());
  }

  @Override
  public Image downloadImage(String imageUrl) {
    var image = getImage(imageUrl);
    var bytes = imageConverter.toBytes(image);
    return Image.builder()
        .name(urlConverter.toFilenameExcludingExtension(imageUrl))
        .format(urlConverter.toExtension(imageUrl))
        .bufferedImage(image)
        .bytes(bytes)
        .hash("blah") // TODO calculate hash here
        .build();
  }

  private BufferedImage getImage(String imageUrl) {
    try {
      var url = URI.create(imageUrl).toURL();
      var image = ImageIO.read(url);
      return rotator.rotateIfRequired(url, image);
    } catch (IOException e) {
      throw new ImageException(e);
    }
  }
}
