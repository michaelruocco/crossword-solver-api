package uk.co.mruoc.cws.usecase;

import java.awt.image.BufferedImage;

public interface ImageCompressor {

  byte[] compressAndResize(BufferedImage image);
}
