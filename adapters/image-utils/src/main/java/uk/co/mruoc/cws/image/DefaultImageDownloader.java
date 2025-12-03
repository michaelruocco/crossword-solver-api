package uk.co.mruoc.cws.image;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import uk.co.mruoc.cws.usecase.ImageDownloader;

@RequiredArgsConstructor
public class DefaultImageDownloader implements ImageDownloader {

  private final ImageRotator rotator;

  public DefaultImageDownloader() {
    this(new ImageRotator());
  }

  @Override
  public BufferedImage downloadImage(String imageUrl) {
    try {
      var url = URI.create(imageUrl).toURL();
      var image = ImageIO.read(url);
      return rotator.rotateIfRequired(url, image);
    } catch (IOException e) {
      throw new ImageException(e);
    }
  }
}
