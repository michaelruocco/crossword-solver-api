package uk.co.mruoc.cws.usecase;

import java.awt.image.BufferedImage;
import uk.co.mruoc.cws.entity.Grid;

public interface GridImageFactory {

  BufferedImage toImage(Grid grid);
}
