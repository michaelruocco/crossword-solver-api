package uk.co.mruoc.cws.solver.textract;

import lombok.RequiredArgsConstructor;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
public class CellProcessor {

    public Mat process(Mat mat) {
        Scalar white = new Scalar(255, 255, 255);
        Scalar black = new Scalar(0, 0, 0);
        Mat background = new Mat(mat.height() * 4, mat.width() * 4, CvType.CV_8UC3, white);
        Mat newCell = new Mat();
        Core.copyMakeBorder(background, newCell, 7, 7, 7, 7, Core.BORDER_CONSTANT, black);
        if (isMostlyDark(mat)) {
            Imgcodecs.imwrite("7g-new-cell.png", newCell);
            return newCell;
        }

        Mat gray = new Mat();
        Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY);
        Imgcodecs.imwrite("7a-gray-cell.png", gray);

        Mat binary = new Mat();
        Imgproc.adaptiveThreshold(
                gray, binary, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 51, 10);
        Imgcodecs.imwrite("7b-binary-cell.png", binary);

        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(
                binary, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        contours = contours.stream().sorted(Comparator.comparingInt(this::getLeftmost)).toList();

        var totalArea = mat.rows() * mat.cols();

        Mat contourOutput = mat.clone();
        Scalar red = new Scalar(0, 0, 255);
        List<Mat> digits = new ArrayList<>();
        int contoursDrawn = 0;
        for (MatOfPoint c : contours) {
            double contourArea = Imgproc.contourArea(c);
            if (contourArea > 0) {
                double percentageOfTotal = (contourArea / totalArea) * 100;
                // System.out.println(contourArea + " " + percentageOfTotal + " of " + totalArea);
                if (percentageOfTotal > 0.75 && percentageOfTotal < 8) {
                    System.out.println(contourArea + " " + percentageOfTotal + " of " + totalArea);
                    Imgproc.drawContours(contourOutput, List.of(c), 0, red, 1);
                    contoursDrawn++;

                    Rect box = Imgproc.boundingRect(c);
                    System.out.println("box x " + box.x + " y" + box.y);
                    double percentageOfHeight = ((double) box.height / binary.height()) * 100;
                    System.out.println("percentage of height " + percentageOfHeight);
                    double percentageOfWidth = ((double) box.width / binary.width()) * 100;
                    System.out.println("percentage of width " + percentageOfWidth);

                    if ((percentageOfHeight < 45 && percentageOfWidth < 45) && (box.x != 0 && box.y != 0)) {
                        Mat digit = binary.submat(box);
                        Imgcodecs.imwrite(String.format("7c-digit-%d.png", contoursDrawn), digit);

                        Mat scaled = new Mat();
                        Imgproc.resize(digit, scaled, new Size(), 6, 6, Imgproc.INTER_NEAREST);
                        Imgcodecs.imwrite(String.format("7d-scaled-digit-%d.png", contoursDrawn), scaled);

                        Mat invertedDigit = new Mat();
                        Core.bitwise_not(scaled, invertedDigit);
                        Imgproc.cvtColor(invertedDigit, invertedDigit, Imgproc.COLOR_GRAY2BGR);
                        Imgcodecs.imwrite(
                                String.format("7f-inverted-digit-%d.png", contoursDrawn), invertedDigit);
                        digits.add(invertedDigit);
                    }
                }
            }
        }
        Imgcodecs.imwrite("7f-cell-with-contours.png", contourOutput);
        // System.out.println("Contours drawn: " + contoursDrawn);

        // System.out.println("new cell " + newCell.width() + " " + newCell.height());
        if (!digits.isEmpty()) {
            Mat digit = horizontalConcat(digits, 25);

            // System.out.println("digit " + digit.width() + " " + digit.height());
            int x = (newCell.width() - digit.width()) / 2;
            int y = (newCell.height() - digit.height()) / 2;
            if ((x >= 0 && x <= newCell.width()) && (y >= 0 && y <= newCell.height())) {
                // System.out.println("x " + x + " y " + y);
                Mat roi = newCell.submat(new Rect(x, y, digit.width(), digit.height()));

                digit.copyTo(roi);
            }
        }

        Imgcodecs.imwrite("7g-new-cell.png", newCell);
        return newCell;
    }

    public Mat horizontalConcat(List<Mat> mats, int spacing) {
        var targetHeight = mats.stream().mapToInt(Mat::height).max().orElseThrow();
        var white = new Scalar(255, 255, 255);

        List<Mat> processedDigits = new ArrayList<>();
        for (Mat mat : mats) {
            Mat copy = mat.clone();

            int topPad = (targetHeight - copy.rows()) / 2;
            int bottomPad = targetHeight - copy.rows() - topPad;
            Mat padded = new Mat();
            Core.copyMakeBorder(copy, padded, topPad, bottomPad, 0, 0, Core.BORDER_CONSTANT, white);

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
            Core.copyMakeBorder(copy, padded, 0, 0, leftPad, rightPad, Core.BORDER_CONSTANT, white);

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

    private boolean isMostlyDark(Mat cellMat) {
        Mat gray = new Mat();
        if (cellMat.channels() > 1) {
            Imgproc.cvtColor(cellMat, gray, Imgproc.COLOR_BGR2GRAY);
        } else {
            gray = cellMat.clone();
        }
        int darkPixels = 0;
        int totalPixels = gray.rows() * gray.cols();
        for (int row = 0; row < gray.rows(); row++) {
            for (int col = 0; col < gray.cols(); col++) {
                double intensity = gray.get(row, col)[0];
                if (intensity < 67) {
                    darkPixels++;
                }
            }
        }
        return darkPixels > totalPixels / 2;
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
}
