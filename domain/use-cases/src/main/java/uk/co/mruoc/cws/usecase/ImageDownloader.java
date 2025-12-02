package uk.co.mruoc.cws.usecase;

import java.awt.image.BufferedImage;

public interface ImageDownloader {

  BufferedImage downloadImage(String imageUrl);
}
