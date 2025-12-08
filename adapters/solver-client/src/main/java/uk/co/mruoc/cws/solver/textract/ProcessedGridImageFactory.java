package uk.co.mruoc.cws.solver.textract;

import lombok.RequiredArgsConstructor;
import uk.co.mruoc.cws.image.MatConverter;

import java.awt.image.BufferedImage;

@RequiredArgsConstructor
public class ProcessedGridImageFactory {

    private final ImageProcessor processor;
    private final GridDimensionsCalculator calculator;
    private final MatConverter matConverter;

    public ProcessedGridImageFactory() {
        this(new ImageProcessor(), new GridDimensionsCalculator(), new MatConverter());
    }

    public BufferedImage toProcessedGridImage(BufferedImage input) {
        var bytes = toProcessedGridImageBytes(input);
        return matConverter.toBufferedImage(bytes);
    }

    public byte[] toProcessedGridImageBytes(BufferedImage input) {
        var grid = processor.extractGrid(input);
        var binary = processor.process(input);
        var dimensions = calculator.calculateDimensions(binary).withGrid(grid);
        var processedGrid = dimensions.getProcessedGrid();
        return matConverter.toPngBytes(processedGrid);
    }
}
