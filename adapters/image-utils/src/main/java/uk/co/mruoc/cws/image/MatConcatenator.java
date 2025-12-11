package uk.co.mruoc.cws.image;

import lombok.RequiredArgsConstructor;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.ToIntFunction;

import static org.opencv.core.Core.BORDER_CONSTANT;

@RequiredArgsConstructor
public class MatConcatenator {

    private static final Scalar WHITE = new Scalar(255, 255, 255);

    private final Scalar backgroundColor;

    public MatConcatenator() {
        this(WHITE);
    }

    public Mat horizontalConcat(Collection<Mat> mats) {
        return horizontalConcat(mats, 0);
    }

    /*public Mat horizontalConcat(Collection<Mat> mats, int spacing) {
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
                  System.out.println("type " + padded.type());
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
    }*/

    public Mat horizontalConcat(Collection<Mat> mats, int spacing) {
        var targetHeight = findMax(mats, Mat::height);
        var paddedMats = padAllToHeight(mats, targetHeight);
        var spacedMats = addSpacingIfRequired(paddedMats, targetHeight, spacing);
        var combined = new Mat();
        Core.hconcat(spacedMats, combined);
        return combined;
    }

    public Mat verticalConcat(List<Mat> mats) {
        return verticalConcat(mats, 0);
    }

    public Mat verticalConcat(List<Mat> mats, int spacing) {
        var targetWidth = findMax(mats, Mat::width);
        var paddedMats = padAllToWidth(mats, targetWidth);
        var spacedMats = addSpacingIfRequired(paddedMats, targetWidth, spacing);
        var combined = new Mat();
        Core.vconcat(spacedMats, combined);
        return combined;
    }

    private int findMax(Collection<Mat> mats, ToIntFunction<Mat> function) {
        return mats.stream().mapToInt(function).max().orElseThrow();
    }

    private List<Mat> padAllToHeight(Collection<Mat> inputs, int targetHeight) {
        return inputs.stream().map(mat -> padToHeight(mat, targetHeight)).toList();
    }

    private Mat padToHeight(Mat input, int targetHeight) {
        var copy = input.clone();
        int topPad = (targetHeight - copy.rows()) / 2;
        int bottomPad = targetHeight - copy.rows() - topPad;
        Mat padded = new Mat();
        Core.copyMakeBorder(copy, padded, topPad, bottomPad, 0, 0, BORDER_CONSTANT, backgroundColor);
        return padded;
    }

    private List<Mat> padAllToWidth(Collection<Mat> inputs, int targetWidth) {
        return inputs.stream().map(mat -> padToWidth(mat, targetWidth)).toList();
    }

    private Mat padToWidth(Mat input, int targetWidth) {
        var copy = input.clone();
        int leftPad = (targetWidth - copy.width()) / 2;
        int rightPad = targetWidth - copy.width() - leftPad;
        var padded = new Mat();
        Core.copyMakeBorder(copy, padded, 0, 0, leftPad, rightPad, BORDER_CONSTANT, backgroundColor);
        return padded;
    }

    private List<Mat> addSpacingIfRequired(List<Mat> mats, int targetSize, int spacing) {
        if (spacing < 1) {
            return mats;
        }
        var type = findType(mats);
        var spacer = Mat.ones(targetSize, spacing, type);
        spacer.setTo(backgroundColor);
        return addSpacing(mats, spacer);
    }

    private int findType(Collection<Mat> mats) {
        return mats.stream().map(Mat::type).findFirst().orElseThrow();
    }

    private List<Mat> addSpacing(List<Mat> inputs, Mat spacer) {
        List<Mat> spacedMats = new ArrayList<>();
        for (int i = 0; i < inputs.size(); i++) {
            spacedMats.add(inputs.get(i));
            if (i < inputs.size() - 1) {
                spacedMats.add(spacer);
            }
        }
        return spacedMats;
    }
}