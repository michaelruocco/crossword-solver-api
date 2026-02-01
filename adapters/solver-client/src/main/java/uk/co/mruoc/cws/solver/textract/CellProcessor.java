package uk.co.mruoc.cws.solver.textract;

import static org.opencv.core.Core.BORDER_CONSTANT;
import static org.opencv.core.CvType.CV_8UC3;
import static org.opencv.imgproc.Imgproc.RETR_LIST;
import static uk.co.mruoc.cws.solver.textract.RectUtils.contains;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

import lombok.RequiredArgsConstructor;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

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
    if (isMostlyDark(input)) {
      return buildNewCell(input, backgroundColor);
    }

    var newCell = buildNewCell(input);
    var digits = findDigits(input);
    if (digits.isEmpty()) {
      return newCell;
    }

    var number = concatenator.horizontalConcat(digits, digitSpacing);
    return centerNumberInCell(newCell, number);
  }

  private Mat buildNewCell(Mat input) {
    return buildNewCell(input, backgroundColor);
  }

  private Mat buildNewCell(Mat input, Scalar color) {
    var height = input.height() * cellScale;
    var width = input.width() * cellScale;
    var background = new Mat(height, width, CV_8UC3, color);
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
        if (intensity < 100) {
          darkPixels++;
        }
      }
    }
    return darkPixels;
  }

  private Collection<Mat> findDigits(Mat input) {
    var gray = converter.toGrayscale(input);
    var binary = converter.toBinary(gray);
    var contours =
        converter.toContours(binary, RETR_LIST).stream()
            .sorted(Comparator.comparingInt(this::getLeftmostX))
            .toList();
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

  private Collection<MatOfPoint> filterLikelyDigitContours(Mat binary, Collection<MatOfPoint> contours) {
    var candidateContours = contours.stream().filter(contour -> isLikelyDigit(binary, contour)).toList();
    return removeDuplicates(candidateContours);
  }

  private Collection<MatOfPoint> removeDuplicates(Collection<MatOfPoint> contours) {
    var deduplicated = new ArrayList<MatOfPoint>();
    for (var contour : contours) {
      var isContained = false;
      var thisBox = Imgproc.boundingRect(contour);
      var otherContours = new ArrayList<>(contours);
      otherContours.remove(contour);
      for (var otherContour : otherContours) {
        var otherBox = Imgproc.boundingRect(otherContour);
        if (contains(otherBox, thisBox)) {
          isContained = true;
        }
      }
      if (!isContained) {
        deduplicated.add(contour);
      }
    }
    return deduplicated;
  }



  private Collection<Mat> toDigits(Mat binary, Collection<MatOfPoint> contours) {
    var candidateContours = filterLikelyDigitContours(binary, contours);
    return candidateContours.stream()
        .map(contour -> toSubmat(binary, contour))
        .map(digit -> converter.scale(digit, digitScale))
        .map(converter::invert)
        .toList();
  }

  private boolean isLikelyDigit(Mat binary, MatOfPoint contour) {
    var contourArea = Imgproc.contourArea(contour);
    if (contourArea <= 0) {
      return false;
    }

    var box = Imgproc.boundingRect(contour);
    if (box.x == 0 || box.y == 0) {
      return false;
    }

    var totalArea = binary.rows() * binary.cols();
    var percentageOfArea = (contourArea / totalArea) * 100;
    if (percentageOfArea <= 0.75 || percentageOfArea >= 8) {
      return false;
    }

    var percentageOfHeight = ((double) box.height / binary.height()) * 100;
    var percentageOfWidth = ((double) box.width / binary.width()) * 100;
    return percentageOfHeight < 45 && percentageOfWidth < 45;
  }

  private Mat toSubmat(Mat binary, MatOfPoint contour) {
    var box = Imgproc.boundingRect(contour);
    return binary.submat(box);
  }

  private Mat centerNumberInCell(Mat cell, Mat number) {
    var x = (cell.width() - number.width()) / 2;
    var y = (cell.height() - number.height()) / 2;
    var roi = cell.submat(new Rect(x, y, number.width(), number.height()));
    number.copyTo(roi);
    return cell;
  }
}
