package uk.co.mruoc.cws.solver.tesseract;

import java.nio.file.Paths;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NumberDetectorFactory {

  public static NumberDetector build() {
    var path = Paths.get("tessdata").toAbsolutePath();
    return new NumberDetector(path.toString());
  }
}
