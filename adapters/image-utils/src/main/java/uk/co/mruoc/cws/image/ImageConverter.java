package uk.co.mruoc.cws.image;

import javax.imageio.ImageIO;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

public class ImageConverter {

  public BufferedImage toRgb(BufferedImage image) {
    if (image.getType() == TYPE_INT_RGB) {
      return image;
    }
    var rgbImage = new BufferedImage(image.getWidth(), image.getHeight(), TYPE_INT_RGB);
    var g = rgbImage.createGraphics();
    g.setComposite(AlphaComposite.SrcOver);
    g.drawImage(image, 0, 0, Color.WHITE, null);
    g.dispose();
    return rgbImage;
  }

  public byte[] toBytes(BufferedImage image) {
    try (var output = new ByteArrayOutputStream()) {
      ImageIO.write(image, "png", output);
      return output.toByteArray();
    } catch (IOException e) {
      throw new ImageException(e);
    }
  }
}
