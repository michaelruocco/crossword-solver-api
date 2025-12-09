package uk.co.mruoc.cws.solver.textract;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nu.pattern.OpenCV;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OpenCvInitializer {
  static {
    OpenCV.loadLocally();
  }

  public static void init() {
    // intentionally blank, just used to trigger class loading at least once
  }
}
