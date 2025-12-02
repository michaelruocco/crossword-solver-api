package uk.co.mruoc.cws.solver.bedrock;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.RequiredArgsConstructor;
import net.sourceforge.tess4j.TessAPI;
import net.sourceforge.tess4j.Tesseract;

@RequiredArgsConstructor
public class TesseractFactory {

  static {
    System.setProperty(
        "jna.library.path",
        "/opt/homebrew/Cellar/tesseract/5.5.1_1/lib:/opt/homebrew/Cellar/leptonica/1.86.0/lib");
  }

  public Tesseract build() {
    Tesseract tesseract = new Tesseract();

    Path tempFile = Path.of(System.getProperty("java.io.tmpdir"), "eng.traineddata");
    try (var in = Tesseract.class.getResourceAsStream("/tesseract-data/eng.traineddata")) {
      Files.copy(in, tempFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    // TODO use project folder rather than local machine
    // System.out.println(tempFile);
    // tesseract.setDatapath(tempFile.getParent().toString());
    tesseract.setDatapath("/opt/homebrew/share/tessdata");
    tesseract.setLanguage("eng");
    tesseract.setVariable("tessedit_char_whitelist", "0123456789");
    tesseract.setOcrEngineMode(TessAPI.TessOcrEngineMode.OEM_LSTM_ONLY);
    tesseract.setPageSegMode(TessAPI.TessPageSegMode.PSM_SINGLE_CHAR);
    return tesseract;
  }
}
