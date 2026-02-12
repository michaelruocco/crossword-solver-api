package uk.co.mruoc.cws.solver.tesseract;

import static org.opencv.core.CvType.CV_8UC3;
import static org.opencv.imgproc.Imgproc.INTER_NEAREST;
import static org.opencv.imgproc.Imgproc.RETR_LIST;
import static uk.co.mruoc.cws.solver.tesseract.RectUtils.removeDuplicates;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import uk.co.mruoc.cws.entity.Cell;
import uk.co.mruoc.cws.entity.Coordinates;

@RequiredArgsConstructor
@Slf4j
public class CellFactory {

  static {
    OpenCvInitializer.init();
  }

  private static final Scalar WHITE = new Scalar(255, 255, 255);

  private final MatConverter converter;
  private final NumberDetector detector;

  public CellFactory(NumberDetector numberDetector) {
    this(new MatConverter(), numberDetector);
  }

  public Cell toCell(Mat input, int x, int y) {
    var coordinates = new Coordinates(x, y);
    if (isMostlyDark(input)) {
      return Cell.blackCell(coordinates);
    }
    var digits = findDigits(input);
    if (digits.isEmpty()) {
      return Cell.whiteCell(coordinates);
    }
    return toId(digits)
        .map(id -> Cell.idCell(coordinates, id))
        .orElseGet(() -> Cell.whiteCell(coordinates));
  }

  private Optional<Integer> toId(Collection<Mat> digits) {
    if (digits.isEmpty()) {
      return Optional.empty();
    }
    var id = new StringBuilder();
    for (var digit : digits) {
      toInt(digit).ifPresent(id::append);
    }
    if (id.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(Integer.parseInt(id.toString()));
  }

  private Optional<Integer> toInt(Mat digit) {
    MatLogger.debug(digit, "initial-digit");
    var height = (int) (digit.height() * 1.5);
    var width = (int) (digit.width() * 1.5);
    var bordered = centerNumberInCell(new Mat(height, width, CV_8UC3, WHITE), digit);
    MatLogger.debug(bordered, "bordered");
    var scaled = converter.scale(bordered, 50);
    MatLogger.debug(scaled, "scaled");
    var number = detector.detect(scaled);
    if (number.isPresent()) {
      return number;
    }

    double inputAspect = digit.width() / (double) digit.height();
    double targetAspect = 0.6;
    int targetAspectWidth = (int) (digit.height() * targetAspect);
    if (digit.width() >= targetAspectWidth) {
      return number;
    }
    log.info("increasing input aspect {} to target aspect {}", inputAspect, targetAspect);
    var widenedDigit = digit.clone();
    Imgproc.resize(
        widenedDigit,
        widenedDigit,
        new Size(targetAspectWidth, digit.height()),
        0,
        0,
        INTER_NEAREST);
    MatLogger.debug(digit, "widened-digit");
    return toInt(widenedDigit);
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
    var binary = toBinary(input);
    var contours = findDigitContours(binary);
    return toDigits(binary, contours);
  }

  private Mat toBinary(Mat input) {
    var gray = converter.toGrayscale(input);
    return converter.toBinary(gray, 19, 7);
  }

  private Collection<MatOfPoint> findDigitContours(Mat input) {
    var contours =
        converter.toContours(input, RETR_LIST).stream()
            .sorted(Comparator.comparingInt(this::getLeftmostX))
            .toList();
    return filterLikelyDigitContours(input, contours);
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

  private Collection<MatOfPoint> filterLikelyDigitContours(
      Mat binary, Collection<MatOfPoint> contours) {
    var candidateContours =
        contours.stream().filter(contour -> isLikelyDigit(binary, contour)).toList();
    return removeDuplicates(candidateContours);
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

  private Collection<Mat> toDigits(Mat binary, Collection<MatOfPoint> contours) {
    return contours.stream()
        .map(contour -> toSubmat(binary, contour))
        .map(converter::invert)
        .toList();
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
