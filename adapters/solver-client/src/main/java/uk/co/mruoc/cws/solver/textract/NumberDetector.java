package uk.co.mruoc.cws.solver.textract;

import static net.sourceforge.tess4j.ITessAPI.TessOcrEngineMode.OEM_LSTM_ONLY;
import static net.sourceforge.tess4j.ITessAPI.TessPageSegMode.PSM_SINGLE_WORD;

import java.io.File;
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
    this(buildTesseract(), new MatConverter());
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
    var dataFolder = new File("./tessdata");
    log.debug("setting up tesseract with data path {} ", dataFolder.getAbsolutePath());
    var tesseract = new Tesseract();
    tesseract.setDatapath(dataFolder.getAbsolutePath());
    tesseract.setVariable("tessedit_char_whitelist", "0123456789");
    tesseract.setPageSegMode(PSM_SINGLE_WORD);
    tesseract.setOcrEngineMode(OEM_LSTM_ONLY);
    tesseract.setLanguage("eng");
    return tesseract;
  }
}
