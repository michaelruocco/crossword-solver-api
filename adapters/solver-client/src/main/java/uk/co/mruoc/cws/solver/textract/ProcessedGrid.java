package uk.co.mruoc.cws.solver.textract;

import lombok.Builder;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.stream.IntStream;

@Builder
public class ProcessedGrid {

    private final GridDimensions dimensions;
    private final Mat grid;

    public Mat toMat(CellProcessor cellProcessor) {
        var rows = IntStream.range(0, dimensions.getNumberOfRows()).mapToObj(y -> getRow(cellProcessor, y)).toList();
        var grid = cellProcessor.verticalConcat(rows, 0);
        Imgcodecs.imwrite("10-grid.png", grid);
        return grid;
    }

    private Mat getRow(CellProcessor cellProcessor, int y) {
        var width = dimensions.getAverageColumnWidth();
        var height = dimensions.getAverageRowHeight();
        var cells =
                IntStream.range(0, dimensions.getNumberOfColumns())
                        .mapToObj(x -> getProcessedCell(cellProcessor, x, y))
                        .map(cell -> resize(cell, width, height))
                        .toList();
        return cellProcessor.horizontalConcat(cells, 0);
    }

    private Mat getProcessedCell(CellProcessor cellProcessor, int x, int y) {
        var cell = dimensions.toCell(grid, x, y);
        // TODO debug cell here if we want to
        //Imgcodecs.imwrite(String.format("6-cell-%d-%d.png", x, y), cell);
        return cellProcessor.process(cell);
    }

    private Mat resize(Mat original, int width, int height) {
        Mat resized = new Mat();
        Size size = new Size(width, height);
        Imgproc.resize(original, resized, size);
        return resized;
    }
}
