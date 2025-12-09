package uk.co.mruoc.cws.solver.textract;

import java.awt.image.BufferedImage;
import lombok.RequiredArgsConstructor;
import org.opencv.core.Mat;
import uk.co.mruoc.cws.image.MatConverter;

@RequiredArgsConstructor
public class ProcessedGridImageFactory {

  private final GridExtractor gridExtractor;
  private final GridDimensionsCalculator calculator;
  private final MatConverter matConverter;

  static {
    OpenCvInitializer.init();
  }

  public ProcessedGridImageFactory() {
    this(new GridExtractor(), new GridDimensionsCalculator(), new MatConverter());
  }

  public byte[] toProcessedGridImageBytes(BufferedImage input) {
    return matConverter.toPngBytes(toProcessedGrid(input));
  }

  public BufferedImage toProcessedGridImage(BufferedImage input) {
    return matConverter.toBufferedImage(toProcessedGrid(input));
  }

  private Mat toProcessedGrid(BufferedImage input) {
    var grid = gridExtractor.extractGrid(input);
    var dimensions = calculator.calculateDimensions(input);
    var processedGrid = ProcessedGrid.builder().dimensions(dimensions).grid(grid).build();
    return processedGrid.toMat(new CellProcessor());
  }
}
