package uk.co.mruoc.cws.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.mruoc.cws.solver.tesseract.NumberDetector;
import uk.co.mruoc.cws.solver.tesseract.TesseractGridExtractor;
import uk.co.mruoc.cws.usecase.GridExtractor;

@Configuration
public class TesseractSolverClientConfig {

  // TODO make tessdata folder path a configuration parameter
  @Bean
  public NumberDetector numberDetector() {
    var path =
        "/Users/michaelruocco/git/github/michaelruocco/crossword-solver/adapters/solver-client/tessdata";
    return new NumberDetector(path);
  }

  @Bean
  public GridExtractor tesseractCellExtractor(NumberDetector numberDetector) {
    return new TesseractGridExtractor(numberDetector);
  }
}
