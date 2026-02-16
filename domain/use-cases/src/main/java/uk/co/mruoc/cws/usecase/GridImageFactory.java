package uk.co.mruoc.cws.usecase;

import uk.co.mruoc.cws.entity.Grid;

import java.awt.image.BufferedImage;

public interface GridImageFactory {

    BufferedImage toImage(Grid grid);
}
