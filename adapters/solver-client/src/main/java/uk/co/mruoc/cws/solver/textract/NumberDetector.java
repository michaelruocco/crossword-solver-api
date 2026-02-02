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
            var image = converter.toBufferedImage(cell);
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
        } catch (TesseractException e) {
            return Optional.empty();
        }
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
            log.debug("setting up tesseract with data path {} ", dataFolder.getAbsolutePath());
            var tesseract = new Tesseract();
            tesseract.setDatapath(dataFolder.getAbsolutePath());
            tesseract.setVariable("tessedit_char_whitelist", "0123456789");
            tesseract.setPageSegMode(PSM_SINGLE_WORD);
            tesseract.setOcrEngineMode(OEM_LSTM_ONLY);
            tesseract.setLanguage("eng");
            return tesseract;
    }
}
