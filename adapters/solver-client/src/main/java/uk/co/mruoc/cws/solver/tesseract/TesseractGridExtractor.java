package uk.co.mruoc.cws.solver.tesseract;

import lombok.RequiredArgsConstructor;
import uk.co.mruoc.cws.entity.Grid;
import uk.co.mruoc.cws.usecase.GridExtractor;
import uk.co.mruoc.cws.usecase.Image;

@RequiredArgsConstructor
public class TesseractGridExtractor implements GridExtractor {

  private final GridFactory gridFactory;

  public TesseractGridExtractor(NumberDetector numberDetector) {
    this(new GridFactory(numberDetector));
  }

  @Override
  public Grid extractGrid(Image image) {
    return gridFactory.toGrid(image.getBufferedImage());
  }
}
