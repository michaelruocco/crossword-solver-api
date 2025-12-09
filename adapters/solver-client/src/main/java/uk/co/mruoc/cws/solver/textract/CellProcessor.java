package uk.co.mruoc.cws.solver.textract;

import static org.opencv.core.Core.BORDER_CONSTANT;
import static org.opencv.core.CvType.CV_8UC3;
import static org.opencv.imgproc.Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C;
import static org.opencv.imgproc.Imgproc.CHAIN_APPROX_SIMPLE;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.COLOR_GRAY2BGR;
import static org.opencv.imgproc.Imgproc.INTER_NEAREST;
import static org.opencv.imgproc.Imgproc.RETR_EXTERNAL;
import static org.opencv.imgproc.Imgproc.THRESH_BINARY_INV;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
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

  public CellProcessor() {
    this(WHITE, BLACK, 4, 7, 6);
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

    var number = horizontalConcat(digits, 25);
    var x = (newCell.width() - number.width()) / 2;
    var y = (newCell.height() - number.height()) / 2;
    var roi = newCell.submat(new Rect(x, y, number.width(), number.height()));
    number.copyTo(roi);
    return newCell;
  }

  public Mat horizontalConcat(Collection<Mat> mats, int spacing) {
    var targetHeight = mats.stream().mapToInt(Mat::height).max().orElseThrow();
    var white = new Scalar(255, 255, 255);

    List<Mat> processedDigits = new ArrayList<>();
    for (Mat mat : mats) {
      Mat copy = mat.clone();

      int topPad = (targetHeight - copy.rows()) / 2;
      int bottomPad = targetHeight - copy.rows() - topPad;
      Mat padded = new Mat();
      Core.copyMakeBorder(copy, padded, topPad, bottomPad, 0, 0, BORDER_CONSTANT, white);

      processedDigits.add(padded);

      if (spacing > 0) {
        Mat spacer = Mat.ones(targetHeight, spacing, padded.type());
        spacer.setTo(white);
        processedDigits.add(spacer);
      }
    }

    if (spacing > 0 && !processedDigits.isEmpty()) {
      processedDigits.removeLast();
    }

    Mat combined = new Mat();
    Core.hconcat(processedDigits, combined);
    return combined;
  }

  public Mat verticalConcat(List<Mat> mats, int spacing) {
    var targetWidth = mats.stream().mapToInt(Mat::width).max().orElseThrow();
    var white = new Scalar(255, 255, 255);

    List<Mat> processedDigits = new ArrayList<>();
    for (Mat mat : mats) {
      Mat copy = mat.clone();

      int leftPad = (targetWidth - copy.cols()) / 2;
      int rightPad = targetWidth - copy.cols() - leftPad;
      Mat padded = new Mat();
      Core.copyMakeBorder(copy, padded, 0, 0, leftPad, rightPad, BORDER_CONSTANT, white);

      processedDigits.add(padded);

      if (spacing > 0) {
        Mat spacer = Mat.ones(targetWidth, spacing, padded.type());
        spacer.setTo(white);
        processedDigits.add(spacer);
      }
    }

    if (spacing > 0 && !processedDigits.isEmpty()) {
      processedDigits.removeLast();
    }

    Mat combined = new Mat();
    Core.vconcat(processedDigits, combined);
    return combined;
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
    var gray = toGrayscale(input);
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

  private int getLeftmost(MatOfPoint contour) {
    int minX = Integer.MAX_VALUE;
    for (Point p : contour.toArray()) {
      if (p.x < minX) {
        minX = (int) p.x;
      }
    }
    return minX;
  }

  private Collection<Mat> findDigits(Mat input) {
    var binary = toBinary(input);
    var contours = toContours(binary);
    return toDigits(binary, contours);
  }

  private Mat toBinary(Mat input) {
    var gray = toGrayscale(input);
    var binary = new Mat();
    Imgproc.adaptiveThreshold(
        gray, binary, 255, ADAPTIVE_THRESH_GAUSSIAN_C, THRESH_BINARY_INV, 51, 10);
    Imgcodecs.imwrite("7b-binary-cell.png", binary);
    return binary;
  }

  private Mat toGrayscale(Mat input) {
    var gray = new Mat();
    Imgproc.cvtColor(input, gray, COLOR_BGR2GRAY);
    return gray;
  }

  private Collection<MatOfPoint> toContours(Mat binary) {
    List<MatOfPoint> contours = new ArrayList<>();
    Imgproc.findContours(binary, contours, new Mat(), RETR_EXTERNAL, CHAIN_APPROX_SIMPLE);
    return contours.stream().sorted(Comparator.comparingInt(this::getLeftmost)).toList();
  }

  private Collection<Mat> toDigits(Mat binary, Collection<MatOfPoint> contours) {
    return contours.stream()
        .map(contour -> extractLikelyDigit(binary, contour))
        .flatMap(Optional::stream)
        .map(this::scaleAndInvertDigit)
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

  private Mat scaleAndInvertDigit(Mat digit) {
    var scaled = new Mat();
    Imgproc.resize(digit, scaled, new Size(), digitScale, digitScale, INTER_NEAREST);

    var inverted = new Mat();
    Core.bitwise_not(scaled, inverted);
    Imgproc.cvtColor(inverted, inverted, COLOR_GRAY2BGR);
    return inverted;
  }
}
