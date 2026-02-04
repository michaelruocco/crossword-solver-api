package uk.co.mruoc.cws.solver.tesseract;

import org.opencv.core.Mat;

public class NoopMatLogger implements IMatLogger {

  @Override
  public void init() {
    // intentionally blank
  }

  @Override
  public void debug(Mat mat, String name) {
    // intentionally blank
  }

  @Override
  public void deleteAll() {
    // intentionally blank
  }
}
