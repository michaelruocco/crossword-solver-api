package uk.co.mruoc.cws.solver.textract;

import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

@Slf4j
public class Temp {

    private final MatConverter matConverter = new MatConverter();

    public Mat drawGridIntersectionsWarped(Mat inputBgr) {
        var gray = new Mat();
        Imgproc.cvtColor(inputBgr, gray, Imgproc.COLOR_BGR2GRAY);

        var warped = matConverter.wrapGrid(inputBgr);
        var cropped = matConverter.crop(warped, 10);
        var grayCropped = matConverter.toGrayscale(cropped);
        var blurred = matConverter.blur(grayCropped);
        var binary = matConverter.toBinary(blurred);
        var smoothed = matConverter.smooth(binary);
        //min area 35 to preserve all numbers if needed
        var cleaned = matConverter.removeNoiseSmallerThan(smoothed, 1500);
        var horizontal = matConverter.toHorizontalGridLines(cleaned);
        var vertical = matConverter.toVerticalGridLines(cleaned);
        var gridLines = matConverter.combine(horizontal, vertical);
        MatLogger.debug(gridLines, "grid-lines");

        Mat intersections = matConverter.toIntersections(gridLines);
        MatLogger.debug(intersections, "intersections");
        var points = matConverter.toPoints(intersections);

        Mat out = cropped.clone();
        for (Point p : points) {
            Imgproc.circle(out, p, 5, new Scalar(0, 0, 255), 5);
        }
        return out;
    }

    private Mat toDarkMask(Mat input) {
        Mat gray = new Mat();
        Imgproc.cvtColor(input, gray, Imgproc.COLOR_BGR2GRAY);

        Mat darkMask = new Mat();
        Imgproc.threshold(gray, darkMask, 0, 255, Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU);
        MatLogger.debug(darkMask, "dark-mask");
        return darkMask;
    }

    private Mat toLightMask(Mat input) {
        Mat gray = new Mat();
        Imgproc.cvtColor(input, gray, Imgproc.COLOR_BGR2GRAY);

        Mat lightMask = new Mat();
        Imgproc.threshold(gray, lightMask, 108, 255, Imgproc.THRESH_BINARY); // adjust 180–230
        MatLogger.debug(lightMask, "light-mask");
        return lightMask;
    }
}
