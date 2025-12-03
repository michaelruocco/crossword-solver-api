package uk.co.mruoc.cws.usecase;

import uk.co.mruoc.cws.entity.Cells;

public interface CellExtractor {

  Cells extractCells(String imageUrl);
}
