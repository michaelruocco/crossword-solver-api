package uk.co.mruoc.cws.solver.textract;

import static org.opencv.core.Core.BORDER_CONSTANT;
import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.core.CvType.CV_8UC3;
import static org.opencv.imgproc.Imgproc.INTER_NEAREST;
import static org.opencv.imgproc.Imgproc.RETR_LIST;
import static uk.co.mruoc.cws.solver.textract.RectUtils.removeDuplicates;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import uk.co.mruoc.cws.entity.Coordinates;
import uk.co.mruoc.cws.entity.NewCell;

@RequiredArgsConstructor
@Slf4j
public class CellProcessor {

  static {
    OpenCvInitializer.init();
  }

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
  private final NumberDetector detector;

  public CellProcessor() {
    this(WHITE, BLACK, 26, 7, 48, 400, new MatConverter(), new MatConcatenator(), new NumberDetector());
  }

  public NewCell toCell(Mat input, int x, int y) {
    if (x == 12 && y == 2) {
      MatLogger.debug(input, "input-cell");
    }
    var coordinates = new Coordinates(x, y);
    if (isMostlyDark(input)) {
      return NewCell.blackCell(coordinates);
    }
    var digits = newFindDigits(input);
    //System.out.println("found " + digits.size() + " digits");
    if (digits.isEmpty()) {
      return NewCell.whiteCell(coordinates);
    }
    return toId(digits)
            .map(id -> NewCell.idCell(coordinates, id))
            .orElseGet(() -> NewCell.whiteCell(coordinates));
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
      double targetAspect = 0.6; // typical digit width/height ratio
      int targetAspectWidth = (int)(digit.height() * targetAspect);
      if (digit.width() >= targetAspectWidth) {
        return number;
      }

      log.info("increasing input aspect {} to target aspect {}", inputAspect, targetAspect);
      var widenedDigit = digit.clone();
      Imgproc.resize(widenedDigit, widenedDigit, new Size(targetAspectWidth, digit.height()), 0, 0, INTER_NEAREST);
      MatLogger.debug(digit, "widened-digit");
      return toInt(widenedDigit);
  }

  public Mat toMat(GridDimensions dimensions, NewCell cell) {
    if (cell.black()) {
      return buildCell(dimensions, BLACK);
    }
    return cell.getId()
            .map(id -> buildCell(dimensions, id))
            .orElseGet(() -> buildCell(dimensions, WHITE));
  }

  private Mat buildCell(GridDimensions dimensions, Scalar color) {
    var width = dimensions.getAverageColumnWidth() * 4;
    var height = dimensions.getAverageRowHeight() * 4;
    var background = new Mat(width, height, CV_8UC1, color);
    var mat = new Mat();
    Core.copyMakeBorder(
            background,
            mat,
            borderSize,
            borderSize,
            borderSize,
            borderSize,
            BORDER_CONSTANT,
            BLACK);
    return mat;
  }

  private Mat buildCell(GridDimensions dimensions, Integer id) {
    var cell = buildCell(dimensions, WHITE);
    var text = Integer.toString(id);
    int fontFace = Imgproc.FONT_HERSHEY_SIMPLEX;
    double fontScale = 5.0; // adjust to fit cell
    int thickness = 20;
    Size textSize = Imgproc.getTextSize(text, fontFace, fontScale, thickness, new int[1]);
    var textX = cell.width() * 0.05;
    var textY = (cell.height() * 0.1) + textSize.height;
    var textLocation = new Point(textX, textY);
    Imgproc.putText(cell, text, textLocation, fontFace, fontScale, foregroundColor, thickness);
    return cell;
  }

  public Mat process(Mat input, int x, int y) {
    if (isMostlyDark(input)) {
      return buildNewCell(input, foregroundColor, 4);
    }

    var digits = findDigits(input);
    if (digits.isEmpty()) {
      return buildNewCell(input, backgroundColor, 4);
    }

    var numberCell = buildNewCell(input);
    var number = concatenator.horizontalConcat(digits, digitSpacing);
    var cell = centerNumberInCell(numberCell, number);
    MatLogger.debug(cell, "old-detection-cell");
    //System.out.println("MRUOCCO " + cell.type() + " " + cell.channels());
    var digit = detector.detect(cell);
    var digitCell = buildNewCell(input, backgroundColor, 4);
    if (digit.isEmpty()) {
      return digitCell;
    }

    var text = Integer.toString(digit.get());
    int fontFace = Imgproc.FONT_HERSHEY_SIMPLEX;
    double fontScale = 5.0; // adjust to fit cell
    int thickness = 20;
    Size textSize = Imgproc.getTextSize(text, fontFace, fontScale, thickness, new int[1]);
    var textX = digitCell.width() * 0.05; //(digitCell.width() - textSize.width) / 6;
    var textY = (digitCell.height() * 0.1) + textSize.height; //(digitCell.height() - textSize.height) / 4;
    var textLocation = new Point(textX, textY);
    Imgproc.putText(digitCell, text, textLocation, fontFace, fontScale, foregroundColor, thickness);
    //MatLogger.debug(digitCell, String.format("clean-digit-cell-%s", text));
    return digitCell;
  }

  private Mat buildNewCell(Mat input) {
    return buildNewCell(input, backgroundColor);
  }

  private Mat buildNewCell(Mat input, Scalar color) {
    return buildNewCell(input, color, cellScale);
  }

  private Mat buildNewCell(Mat input, Scalar color, int scale) {
    var height = input.height() * scale;
    var width = input.width() * scale;
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

  private Collection<Mat> newFindDigits(Mat input) {
    var binary = toBinary(input);
    var contours = findDigitContours(binary);
    return newToDigits(binary, contours);
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

  private Collection<MatOfPoint> filterLikelyDigitContours(Mat binary, Collection<MatOfPoint> contours) {
    var candidateContours = contours.stream().filter(contour -> isLikelyDigit(binary, contour)).toList();
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

  private Collection<Mat> newToDigits(Mat binary, Collection<MatOfPoint> contours) {
    return contours.stream()
            .map(contour -> toSubmat(binary, contour))
            .map(converter::invert)
            .toList();
  }

  private Collection<Mat> toDigits(Mat binary, Collection<MatOfPoint> contours) {
    return contours.stream()
            .map(contour -> toSubmat(binary, contour))
            .map(digit -> converter.scale(digit, digitScale))
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
