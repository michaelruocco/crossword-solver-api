package uk.co.mruoc.cws.solver.tesseract;

import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.opencv.core.Mat;
import uk.co.mruoc.cws.entity.Cell;
import uk.co.mruoc.cws.entity.Cells;
import uk.co.mruoc.cws.entity.Grid;

@RequiredArgsConstructor
public class GridFactory {

  static {
    OpenCvInitializer.init();
  }

  private final GridExtractor gridExtractor;
  private final GridDimensionsCalculator calculator;
  private final CellFactory cellFactory;

  public GridFactory() {
    this(new NumberDetector());
  }

  public GridFactory(NumberDetector numberDetector) {
    this(new GridExtractor(), new GridDimensionsCalculator(), new CellFactory(numberDetector));
  }

  public Grid toGrid(BufferedImage input) {
    var grid = gridExtractor.extractGrid(input);
    return toGrid(grid);
  }

  private Grid toGrid(Mat grid) {
    var dimensions = calculator.calculateDimensions(grid);
    var columnWidth = dimensions.getAverageColumnWidth();
    var rowHeight = dimensions.getAverageRowHeight();
    return new Grid(toCells(grid, dimensions), columnWidth, rowHeight);
  }

  private Cells toCells(Mat grid, GridDimensions dimensions) {
    return new Cells(
        IntStream.iterate(dimensions.getNumberOfRows() - 1, i -> i >= 0, i -> i - 1)
            .mapToObj(y -> toRowCells(grid, dimensions, y))
            .flatMap(Collection::stream)
            .toList());
  }

  private Collection<Cell> toRowCells(Mat grid, GridDimensions dimensions, int y) {
    return IntStream.range(0, dimensions.getNumberOfColumns())
        .mapToObj(x -> toCell(grid, dimensions, x, y))
        .toList();
  }

  private Cell toCell(Mat grid, GridDimensions dimensions, int x, int y) {
    var cellMat = dimensions.toCell(grid, x, y);
    return cellFactory.toCell(cellMat, x, y);
  }
}
