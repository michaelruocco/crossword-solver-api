package uk.co.mruoc.cws.solver.textract;

import lombok.RequiredArgsConstructor;
import nu.pattern.OpenCV;
import uk.co.mruoc.cws.image.MatConverter;

import java.awt.image.BufferedImage;

@RequiredArgsConstructor
public class ProcessedGridImageFactory {

    private final GridExtractor gridExtractor;
    private final GridDimensionsCalculator calculator;
    private final MatConverter matConverter;

    public ProcessedGridImageFactory() {
        this(new GridExtractor(), new GridDimensionsCalculator(), new MatConverter());
        OpenCV.loadLocally();
    }

    public byte[] toProcessedGridImageBytes(BufferedImage input) {
        var grid = gridExtractor.extractGrid(input);
        var dimensions = calculator.calculateDimensions(input);
        var processedGrid = ProcessedGrid.builder()
                .dimensions(dimensions)
                .grid(grid)
                .build();
        return matConverter.toPngBytes(processedGrid.toMat(new CellProcessor()));
    }
}
