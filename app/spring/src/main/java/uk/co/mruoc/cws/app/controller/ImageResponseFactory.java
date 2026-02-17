package uk.co.mruoc.cws.app.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import javax.imageio.ImageIO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class ImageResponseFactory {

  public ResponseEntity<byte[]> toResponse(BufferedImage image) {
    return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(toBytes(image));
  }

  private byte[] toBytes(BufferedImage image) {
    try (var stream = new ByteArrayOutputStream()) {
      ImageIO.write(image, "png", stream);
      return stream.toByteArray();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
