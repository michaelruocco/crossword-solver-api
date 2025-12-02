package uk.co.mruoc.cws.usecase;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import javax.imageio.ImageIO;

public class DefaultImageDownloader implements ImageDownloader {

  @Override
  public BufferedImage downloadImage(String imageUrl) {
    try {
      BufferedImage img = readImageFromUrl(imageUrl);
      int orientation = readExifOrientation(imageUrl);
      return rotateImageIfNeeded(img, orientation);
    } catch (Exception e) {
      throw new RuntimeException("Failed to download or process image: " + imageUrl, e);
    }
  }

  // ------------------- Private Helpers -------------------

  private BufferedImage readImageFromUrl(String url) throws IOException {
    return ImageIO.read(URI.create(url).toURL());
  }

  private int readExifOrientation(String uri) {
    try {
      var url = URI.create(uri).toURL();
      try (var is = url.openStream()) {
        Metadata metadata = ImageMetadataReader.readMetadata(is);
        ExifIFD0Directory dir = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
        if (dir != null && dir.containsTag(ExifIFD0Directory.TAG_ORIENTATION)) {
          return dir.getInt(ExifIFD0Directory.TAG_ORIENTATION);
        }
      } catch (Exception ignored) {
        // If metadata reading fails, just return default orientation
      }
      return 1; // Normal orientation
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  private BufferedImage rotateImageIfNeeded(BufferedImage img, int orientation) {
    int w = img.getWidth();
    int h = img.getHeight();
    AffineTransform tx = new AffineTransform();

    switch (orientation) {
      case 6: // 90° clockwise
        tx.translate(h, 0);
        tx.rotate(Math.PI / 2);
        break;
      case 3: // 180°
        tx.translate(w, h);
        tx.rotate(Math.PI);
        break;
      case 8: // 90° counter-clockwise
        tx.translate(0, w);
        tx.rotate(-Math.PI / 2);
        break;
      default: // 1 = normal, no rotation
        return img;
    }

    BufferedImage rotated =
        new BufferedImage(
            (orientation == 6 || orientation == 8) ? h : w,
            (orientation == 6 || orientation == 8) ? w : h,
            img.getType());
    Graphics2D g = rotated.createGraphics();
    g.drawImage(img, tx, null);
    g.dispose();
    return rotated;
  }
}
