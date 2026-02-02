package uk.co.mruoc.cws.solver.textract;

import lombok.RequiredArgsConstructor;
import uk.co.mruoc.cws.entity.Cells;
import uk.co.mruoc.cws.usecase.CellExtractor;
import uk.co.mruoc.cws.usecase.Image;

@RequiredArgsConstructor
public class TesseractCellExtractor implements CellExtractor {

  private final GridFactory gridFactory;

  public TesseractCellExtractor() {
    this(new GridFactory());
  }

  @Override
  public Cells extractCells(Image image) {
    var grid = gridFactory.toGrid(image.getBufferedImage());
    return grid.cells();
  }
}
