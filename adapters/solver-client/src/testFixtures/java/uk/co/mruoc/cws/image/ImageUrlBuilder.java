package uk.co.mruoc.cws.image;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageUrlBuilder {

  public static String toUrl(String filename) {
    return String.format("https://hackathon.caci.co.uk/images/%s", filename);
  }
}
