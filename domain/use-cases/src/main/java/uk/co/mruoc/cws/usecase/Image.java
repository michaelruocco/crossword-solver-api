package uk.co.mruoc.cws.usecase;

import java.awt.image.BufferedImage;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Image {

  private final String name;
  private final String format;
  private final String hash;
  private final byte[] bytes;
  private final BufferedImage bufferedImage;
}
