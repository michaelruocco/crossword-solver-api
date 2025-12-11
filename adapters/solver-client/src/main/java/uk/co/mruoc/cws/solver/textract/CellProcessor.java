package uk.co.mruoc.cws.solver.textract;

import static org.opencv.core.Core.BORDER_CONSTANT;
import static org.opencv.core.CvType.CV_8UC3;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import uk.co.mruoc.cws.image.MatConcatenator;
import uk.co.mruoc.cws.image.MatConverter;

@RequiredArgsConstructor
public class CellProcessor {

  private static final Scalar WHITE = new Scalar(255, 255, 255);
  private static final Scalar BLACK = new Scalar(0, 0, 0);

  private final Scalar backgroundColor;
  private final Scalar foregroundColor;
  private final int cellScale;
  private final int borderSize;
  private final int digitScale;
  private final int digitSpacing;

  private final MatConverter converter;
  private final MatConcatenator concatenator;

  public CellProcessor() {
    this(WHITE, BLACK, 4, 7, 6, 25, new MatConverter(), new MatConcatenator());
  }

  public Mat process(Mat input) {
    var newCell = buildNewCell(input);
    if (isMostlyDark(input)) {
      return newCell;
    }

    var digits = findDigits(input);
    if (digits.isEmpty()) {
      return newCell;
    }

    var number = concatenator.horizontalConcat(digits, digitSpacing);
    return centerNumberInCell(newCell, number);
  }

  private Mat buildNewCell(Mat input) {
    var height = input.height() * cellScale;
    var width = input.width() * cellScale;
    var background = new Mat(height, width, CV_8UC3, backgroundColor);
    var newCell = new Mat();
    Core.copyMakeBorder(
        background,
        newCell,
        borderSize,
        borderSize,
        borderSize,
        borderSize,
        BORDER_CONSTANT,
        foregroundColor);
    return newCell;
  }

  private boolean isMostlyDark(Mat input) {
    var gray = converter.toGrayscale(input);
    var totalPixels = gray.rows() * gray.cols();
    var darkPixels = countDarkPixels(gray);
    return darkPixels > totalPixels / 2;
  }

  private int countDarkPixels(Mat input) {
    var darkPixels = 0;
    for (var r = 0; r < input.rows(); r++) {
      for (var c = 0; c < input.cols(); c++) {
        var intensity = input.get(r, c)[0];
        if (intensity < 67) {
          darkPixels++;
        }
      }
    }
    return darkPixels;
  }

  private Collection<Mat> findDigits(Mat input) {
    var gray = converter.toGrayscale(input);
    var binary = converter.toBinary(gray);
    var contours = converter.toContours(binary).stream().sorted(Comparator.comparingInt(this::getLeftmostX)).toList();;
    return toDigits(binary, contours);
  }

  private int getLeftmostX(MatOfPoint contour) {
    int minX = Integer.MAX_VALUE;
    for (Point p : contour.toArray()) {
      if (p.x < minX) {
        minX = (int) p.x;
      }
    }
    return minX;
  }

  private Collection<Mat> toDigits(Mat binary, Collection<MatOfPoint> contours) {
    return contours.stream()
        .map(contour -> extractLikelyDigit(binary, contour))
        .flatMap(Optional::stream)
        .map(digit -> converter.scale(digit, digitScale))
        .map(converter::invert)
        .toList();
  }

  private Optional<Mat> extractLikelyDigit(Mat binary, MatOfPoint contour) {
    var contourArea = Imgproc.contourArea(contour);
    if (contourArea <= 0) {
      return Optional.empty();
    }

    var box = Imgproc.boundingRect(contour);
    if (box.x == 0 && box.y == 0) {
      return Optional.empty();
    }

    var totalArea = binary.rows() * binary.cols();
    var percentageOfArea = (contourArea / totalArea) * 100;
    if (percentageOfArea <= 0.75 || percentageOfArea >= 8) {
      return Optional.empty();
    }

    var percentageOfHeight = ((double) box.height / binary.height()) * 100;
    var percentageOfWidth = ((double) box.width / binary.width()) * 100;
    if (percentageOfHeight >= 45 || percentageOfWidth >= 45) {
      return Optional.empty();
    }

    return Optional.of(binary.submat(box));
  }

  private Mat centerNumberInCell(Mat cell, Mat number) {
    var x = (cell.width() - number.width()) / 2;
    var y = (cell.height() - number.height()) / 2;
    var roi = cell.submat(new Rect(x, y, number.width(), number.height()));
    number.copyTo(roi);
    return cell;
  }
}
