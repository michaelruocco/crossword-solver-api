package uk.co.mruoc.cws.solver.tesseract;

import org.opencv.core.Mat;

public interface IMatLogger {

  void init();

  void debug(Mat mat, String name);

  void deleteAll();
}
