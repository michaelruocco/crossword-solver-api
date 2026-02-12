package uk.co.mruoc.cws.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.co.mruoc.cws.solver.tesseract.NumberDetector;
import uk.co.mruoc.cws.solver.tesseract.TesseractGridExtractor;
import uk.co.mruoc.cws.usecase.GridExtractor;

@Configuration
public class TesseractSolverClientConfig {

  @Bean
  public NumberDetector numberDetector(@Value("${tesseract.data.path}") String dataPath) {
    return new NumberDetector(dataPath);
  }

  @Bean
  public GridExtractor tesseractCellExtractor(NumberDetector numberDetector) {
    return new TesseractGridExtractor(numberDetector);
  }
}
