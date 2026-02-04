package uk.co.mruoc.cws.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.mruoc.cws.solver.tesseract.NumberDetector;
import uk.co.mruoc.cws.solver.tesseract.TesseractGridExtractor;
import uk.co.mruoc.cws.usecase.GridExtractor;

@Configuration
public class TesseractSolverClientConfig {

  @Bean
  public NumberDetector numberDetector() {
    var path =
        "/Users/michaelruocco/git/github/michaelruocco/crossword-solver/adapters/solver-client/tessdata";
    return new NumberDetector(path);
  }

  // TODO add configurable tesseract instance as a bean and pass into cell extractor / grid factory
  // / number detector
  @Bean
  public GridExtractor tesseractCellExtractor(NumberDetector numberDetector) {
    return new TesseractGridExtractor(numberDetector);
  }
}
