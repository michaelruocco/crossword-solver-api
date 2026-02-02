package uk.co.mruoc.cws.solver.textract;

import java.awt.image.BufferedImage;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Mat;

@Slf4j
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
    var dimensions = calculator.calculateDimensions(grid);
    log.info("average column width {} and height {}", dimensions.getAverageColumnWidth(), dimensions.getAverageRowHeight());
    return toMat(grid, dimensions);
  }

  public Mat toMat(Mat grid, GridDimensions dimensions) {
    var rows = IntStream.iterate(dimensions.getNumberOfRows() - 1, i -> i >= 0, i -> i - 1)
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
    var cellMat = dimensions.toCell(grid, x, y);
    var cell = cellProcessor.toCell(cellMat, x, y);
    return cellProcessor.toMat(dimensions, cell);

    //return cellProcessor.process(cellMat, x, y);
     // return cellProcessor.process(cell);
  }
}
