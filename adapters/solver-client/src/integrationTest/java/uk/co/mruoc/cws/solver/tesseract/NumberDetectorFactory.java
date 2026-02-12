package uk.co.mruoc.cws.solver.tesseract;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.nio.file.Paths;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NumberDetectorFactory {

    public static NumberDetector build() {
        var path = Paths.get("tessdata").toAbsolutePath();
        return new NumberDetector(path.toString());
    }
}
