package uk.co.mruoc.cws.usecase.puzzle;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import uk.co.mruoc.cws.usecase.Image;

@RequiredArgsConstructor
public class ImageValidator {

  private final int maxSizeInMB;
  private final Collection<String> allowedFormats;

  public ImageValidator() {
    this(20, List.of("png", "jpg", "jpeg"));
  }

  public void validate(Image image) {
    if (Objects.isNull(image.getName())) {
      throw new IllegalArgumentException("file name is mandatory");
    }
    if (allowedFormats.contains(image.getFormat())) {
      throw new IllegalArgumentException(
          String.format(
              "image format %s is not an allowed format %s",
              image.getFormat(), String.join(",", allowedFormats)));
    }
    if (image.getSizeInMB() > 20) {
      throw new IllegalArgumentException(
          String.format(
              "image file size %f larger than max allowed file size %d",
              image.getSizeInMB(), maxSizeInMB));
    }
  }
}
