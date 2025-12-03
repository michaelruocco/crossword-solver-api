package uk.co.mruoc.cws.image;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.image.BufferedImage;

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
}
