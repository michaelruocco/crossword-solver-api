package uk.co.mruoc.cws.solver.textract;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static net.sourceforge.tess4j.ITessAPI.TessOcrEngineMode.OEM_LSTM_ONLY;
import static net.sourceforge.tess4j.ITessAPI.TessPageSegMode.PSM_SINGLE_CHAR;
import static net.sourceforge.tess4j.ITessAPI.TessPageSegMode.PSM_SINGLE_WORD;

@Slf4j
@RequiredArgsConstructor
public class NumberDetector {

    private final Tesseract tesseract;
    private final MatConverter converter;
    private AtomicInteger temp = new AtomicInteger(0);

    public NumberDetector() {
        this(buildTesseract(), new MatConverter());
    }

    public Optional<Integer> detect(Mat cell) {
        try {
            var t = temp(cell);
            var output = new File(String.format("integration-test-files/debug-images/cell-%d.png", temp.getAndIncrement()));
            var image = converter.toBufferedImage(t);
            ImageIO.write(image, "png", output);
            var raw = tesseract.doOCR(image);
            var text = raw.trim();
            log.info("text {}", text);
            if (text.isEmpty()) {
                return Optional.empty();
            }
            if (!text.matches("\\d+")) {
                return Optional.empty();
            }
            return Optional.of(Integer.parseInt(text));
        } catch (IOException | TesseractException e) {
            return Optional.empty();
        }
    }

    private Mat temp(Mat input) {
        Mat gray = new Mat();
        Imgproc.cvtColor(input, gray, Imgproc.COLOR_BGR2GRAY); // convert to grayscale

        Mat binary = new Mat();
        Imgproc.threshold(gray, binary, 100, 255, Imgproc.THRESH_BINARY); // simple threshold

// Find contours
        var contours = findNumberContours(binary);
// System.out.println("got " + contours.size() + " contours");
// Draw bounding boxes on original color image
        for (MatOfPoint contour : contours) {
            Rect rect = Imgproc.boundingRect(contour);
            //System.out.println("x " + rect.x + " y " + rect.y + " width " + rect.width + " height " + rect.height);
            Imgproc.rectangle(input, rect.tl(), rect.br(), new Scalar(0, 0, 255), 5); // red box
            if (rect.width > 1250) {
                System.out.println("splitting");
                Mat roi = new Mat(input, rect); // crop contour area

                int midX = rect.width / 2;
                Mat leftHalf = new Mat(roi, new Rect(0, 0, midX, rect.height));
                Mat rightHalf = new Mat(roi, new Rect(midX, 0, rect.width - midX, rect.height));
                int gap = 100;

                Mat expanded = new Mat(rect.height, rect.width + gap, roi.type(), new Scalar(255, 255, 255));
                // Left half goes at start
                leftHalf.copyTo(expanded.submat(new Rect(0, 0, leftHalf.width(), leftHalf.height())));

// Right half goes after leftHalf + gap
                rightHalf.copyTo(expanded.submat(new Rect(leftHalf.width() + gap, 0, rightHalf.width(), rightHalf.height())));

                int pasteX = rect.x - (gap / 2);
                int pasteY = rect.y;

// Make sure we don’t go outside the image boundary
                Rect pasteRect = new Rect(pasteX, pasteY, expanded.width(), expanded.height());
                expanded.copyTo(new Mat(input, pasteRect));

                //System.out.println("drawing");
            }
        }
        return input;
    }

    private Collection<MatOfPoint> findNumberContours(Mat input) {
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(input, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        contours = contours.stream().filter(contour -> !isBorder(contour)).toList();
        return RectUtils.removeDuplicates(contours);
    }

    private boolean isBorder(MatOfPoint contour) {
        Rect rect = Imgproc.boundingRect(contour);
        return rect.x <= 7 && rect.y <= 7;
    }

    private static Tesseract buildTesseract() {
            var dataFolder = new File("./tessdata");
            System.out.println(dataFolder.getAbsolutePath());
            var tesseract = new Tesseract();
            tesseract.setDatapath(dataFolder.getAbsolutePath());
            tesseract.setVariable("tessedit_char_whitelist", "0123456789");
            tesseract.setPageSegMode(PSM_SINGLE_WORD);
            tesseract.setOcrEngineMode(OEM_LSTM_ONLY);
            tesseract.setLanguage("eng");
            return tesseract;
    }
}
