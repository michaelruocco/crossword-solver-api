package uk.co.mruoc.cws.solver.tesseract;

import static net.sourceforge.tess4j.ITessAPI.TessOcrEngineMode.OEM_LSTM_ONLY;
import static net.sourceforge.tess4j.ITessAPI.TessPageSegMode.PSM_SINGLE_WORD;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.Mat;

@Slf4j
@RequiredArgsConstructor
public class NumberDetector {

  private final Tesseract tesseract;
  private final MatConverter converter;

  public NumberDetector() {
    this(buildTesseract());
  }

  public NumberDetector(String dataFolderPath) {
    this(buildTesseract(dataFolderPath));
  }

  public NumberDetector(Tesseract tesseract) {
    this(tesseract, new MatConverter());
  }

  public Optional<Integer> detect(Mat cell) {
    try {
      var image = converter.toBufferedImage(cell);
      var raw = tesseract.doOCR(image);
      var text = raw.trim();
      log.info("text {}", text);
      if (text.isEmpty()) {
        return Optional.empty();
      }
      if (!text.matches("\\d+")) {
        return Optional.empty();
      }
      return Optional.of(Integer.parseInt(text));
    } catch (TesseractException e) {
      log.error(e.getMessage(), e);
      return Optional.empty();
    }
  }

  private static Tesseract buildTesseract() {
    try (var resourceStream =
        NumberDetector.class.getResourceAsStream("/tessdata/eng.traineddata")) {
      Objects.requireNonNull(resourceStream);
      var dataFolder = Files.createTempDirectory("tessdata");
      Files.copy(resourceStream, dataFolder.resolve("eng.traineddata"));
      return buildTesseract(dataFolder.toAbsolutePath().toString());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static Tesseract buildTesseract(String dataFolderPath) {
    var tesseract = new Tesseract();
    log.info("setting up tesseract with data folder path {} ", dataFolderPath);
    tesseract.setDatapath(dataFolderPath);
    tesseract.setVariable("tessedit_char_whitelist", "0123456789");
    tesseract.setVariable("user_defined_dpi", "300");
    tesseract.setPageSegMode(PSM_SINGLE_WORD);
    tesseract.setOcrEngineMode(OEM_LSTM_ONLY);
    tesseract.setLanguage("eng");
    return tesseract;
  }
}
