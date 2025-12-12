package uk.co.mruoc.cws.solver.textract;

import java.awt.image.BufferedImage;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.opencv.core.Mat;

@RequiredArgsConstructor
public class ProcessedGridImageFactory {

  static {
    OpenCvInitializer.init();
  }

  private final MatConverter matConverter;
  private final GridExtractor gridExtractor;
  private final GridDimensionsCalculator calculator;
  private final MatConcatenator matConcatenator;
  private final CellProcessor cellProcessor;

  public ProcessedGridImageFactory() {
    this(
        new MatConverter(),
        new GridExtractor(),
        new GridDimensionsCalculator(),
        new MatConcatenator(),
        new CellProcessor());
  }

  public byte[] toProcessedGridImageBytes(byte[] bytes) {
    return matConverter.toPngBytes(toProcessedGrid(bytes));
  }

  public BufferedImage toProcessedGridImage(BufferedImage input) {
    return matConverter.toBufferedImage(toProcessedGrid(input));
  }

  private Mat toProcessedGrid(byte[] bytes) {
    var input = matConverter.toMat(bytes);
    var grid = gridExtractor.extractGrid(input);
    var dimensions = calculator.calculateDimensions(grid);
    return toMat(grid, dimensions);
  }

  private Mat toProcessedGrid(BufferedImage input) {
    var grid = gridExtractor.extractGrid(input);
    var dimensions = calculator.calculateDimensions(input);
    return toMat(grid, dimensions);
  }

  public Mat toMat(Mat grid, GridDimensions dimensions) {
    var rows =
        IntStream.range(0, dimensions.getNumberOfRows())
            .mapToObj(y -> toRow(grid, dimensions, y))
            .toList();
    return matConcatenator.verticalConcat(rows);
  }

  private Mat toRow(Mat grid, GridDimensions dimensions, int y) {
    var width = dimensions.getAverageColumnWidth();
    var height = dimensions.getAverageRowHeight();
    var cells =
        IntStream.range(0, dimensions.getNumberOfColumns())
            .mapToObj(x -> toProcessedCell(grid, dimensions, x, y))
            .map(cell -> matConverter.resize(cell, width, height))
            .toList();
    return matConcatenator.horizontalConcat(cells);
  }

  private Mat toProcessedCell(Mat grid, GridDimensions dimensions, int x, int y) {
    var cell = dimensions.toCell(grid, x, y);
    return cellProcessor.process(cell);
  }
}
