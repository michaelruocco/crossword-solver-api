package uk.co.mruoc.cws.solver.bedrock;

import java.util.ArrayList;
import java.util.Collection;
import lombok.RequiredArgsConstructor;
import net.sourceforge.tess4j.Tesseract;
import uk.co.mruoc.cws.entity.Coordinates;
import uk.co.mruoc.cws.entity.Id;
import uk.co.mruoc.cws.entity.Word;
import uk.co.mruoc.cws.entity.Words;
import uk.co.mruoc.cws.usecase.ImageDownloader;
import uk.co.mruoc.cws.usecase.WordExtractor;

@RequiredArgsConstructor
public class TesseractWordExtractor implements WordExtractor {

  private final ImageDownloader downloader;
  private final ImageProcessor processor;
  private final GridDimensionsCalculator calculator;
  private final TesseractNumberDetector detector;

  public TesseractWordExtractor(ImageDownloader downloader, Tesseract tesseract) {
    this(
        downloader,
        new ImageProcessor(),
        new GridDimensionsCalculator(),
        new TesseractNumberDetector(tesseract));
  }

  @Override
  public Words extractWords(String imageUrl) {
    var image = downloader.downloadImage(imageUrl);
    var grid = processor.extractGrid(image);
    var binary = processor.process(image);
    var dimensions = calculator.calculateDimensions(binary).withGrid(grid);
    Collection<Word> words = new ArrayList<>();
    for (int y = 0; y < dimensions.getNumberOfRows(); y++) {
      for (int x = 0; x < dimensions.getNumberOfColumns(); x++) {
        var number = detector.toNumberIfPresent(dimensions.getProcessedCell(x, y));
        if (number.isPresent()) {
          System.out.println("got number " + number + " at x " + x + " y " + y);
          var word =
              Word.builder()
                  .id(new Id(number.get(), null))
                  .coordinates(new Coordinates(x, y))
                  .length(-1)
                  .build();
          words.add(word);
        }
      }
    }
    return new Words(words);
  }
}
