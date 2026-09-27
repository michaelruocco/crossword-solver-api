package uk.co.mruoc.cws.image;

import static com.drew.metadata.exif.ExifDirectoryBase.TAG_ORIENTATION;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class ImageRotator {

  private static final int NORMAL_ORIENTATION = 1;
  private static final int NINETY_DEGREES_CLOCKWISE = 6;
  private static final int ONE_HUNDRED_AND_EIGHTY_DEGREES = 3;
  private static final int NINETY_DEGREES_ANTI_CLOCKWISE = 8;

  public BufferedImage rotateIfRequired(URL url, BufferedImage image) {
    var orientation = readOrientation(url);
    if (orientation == NORMAL_ORIENTATION) {
      return image;
    }
    var transform = toTransform(image, orientation);
    return rotate(image, orientation, transform);
  }

  private int readOrientation(URL url) {
    try (var stream = url.openStream()) {
      var metadata = ImageMetadataReader.readMetadata(stream);
      var directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
      return toOrientation(directory);
    } catch (IOException | ImageProcessingException e) {
      throw new ImageException(e);
    }
  }

  private int toOrientation(ExifIFD0Directory directory) {
    try {
      if (directory == null || !directory.containsTag(TAG_ORIENTATION)) {
        return NORMAL_ORIENTATION;
      }
      return directory.getInt(TAG_ORIENTATION);
    } catch (MetadataException e) {
      throw new ImageException(e);
    }
  }

  private AffineTransform toTransform(BufferedImage image, int orientation) {
    int width = image.getWidth();
    int height = image.getHeight();
    var transform = new AffineTransform();
    var tx = toTx(orientation, width, height);
    var ty = toTy(orientation, width, height);
    transform.translate(tx, ty);
    transform.rotate(toTheta(orientation));
    return transform;
  }

  private int toTx(int orientation, int width, int height) {
    return switch (orientation) {
      case NINETY_DEGREES_CLOCKWISE -> height;
      case ONE_HUNDRED_AND_EIGHTY_DEGREES -> width;
      default -> 0;
    };
  }

  private int toTy(int orientation, int width, int height) {
    return switch (orientation) {
      case ONE_HUNDRED_AND_EIGHTY_DEGREES -> height;
      case NINETY_DEGREES_ANTI_CLOCKWISE -> width;
      default -> 0;
    };
  }

  private double toTheta(int orientation) {
    return switch (orientation) {
      case NINETY_DEGREES_CLOCKWISE -> Math.PI / 2;
      case ONE_HUNDRED_AND_EIGHTY_DEGREES -> Math.PI;
      case NINETY_DEGREES_ANTI_CLOCKWISE -> -Math.PI / 2;
      default -> 0;
    };
  }

  private BufferedImage rotate(BufferedImage image, int orientation, AffineTransform transform) {
    var width = image.getWidth();
    var height = image.getHeight();
    var isNinetyDegreeRotation = needsNinetyDegreeRotation(orientation);
    var newWidth = isNinetyDegreeRotation ? height : width;
    var newHeight = isNinetyDegreeRotation ? width : height;
    var rotated = new BufferedImage(newWidth, newHeight, image.getType());
    var g = rotated.createGraphics();
    g.drawImage(image, transform, null);
    g.dispose();
    return rotated;
  }

  private boolean needsNinetyDegreeRotation(int orientation) {
    return (orientation == NINETY_DEGREES_CLOCKWISE
        || orientation == NINETY_DEGREES_ANTI_CLOCKWISE);
  }
}
