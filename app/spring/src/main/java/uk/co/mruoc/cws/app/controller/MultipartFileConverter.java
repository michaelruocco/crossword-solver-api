package uk.co.mruoc.cws.app.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Optional;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import uk.co.mruoc.cws.image.ImageConverter;
import uk.co.mruoc.cws.usecase.HashFactory;
import uk.co.mruoc.cws.usecase.Image;

@RequiredArgsConstructor
public class MultipartFileConverter {

  private final ImageConverter imageConverter;
  private final HashFactory hashFactory;

  public MultipartFileConverter() {
    this(new ImageConverter(), new HashFactory());
  }

  public Image toImage(MultipartFile file) {
    var image = toBufferedImage(file);
    var bytes = imageConverter.toBytes(image);
    var filename = Optional.ofNullable(file.getOriginalFilename()).orElseThrow();
    return Image.builder()
        .name(removeExtension(filename))
        .format(toExtension(filename))
        .bufferedImage(image)
        .bytes(bytes)
        .hash(hashFactory.toHash(bytes))
        .build();
  }

  private BufferedImage toBufferedImage(MultipartFile file) {
    try {
      return Optional.ofNullable(ImageIO.read(file.getInputStream())).orElseThrow();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private String removeExtension(String filename) {
    return filename.substring(0, filename.lastIndexOf('.'));
  }

  private String toExtension(String filename) {
    return filename.substring(filename.lastIndexOf('.'));
  }
}
