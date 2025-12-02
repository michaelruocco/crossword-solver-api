package uk.co.mruoc.cws.solver.textract;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.AnalyzeDocumentRequest;
import software.amazon.awssdk.services.textract.model.AnalyzeDocumentResponse;
import software.amazon.awssdk.services.textract.model.Block;
import software.amazon.awssdk.services.textract.model.BlockType;
import software.amazon.awssdk.services.textract.model.Document;
import software.amazon.awssdk.services.textract.model.FeatureType;
import software.amazon.awssdk.services.textract.model.Relationship;
import software.amazon.awssdk.services.textract.model.RelationshipType;
import uk.co.mruoc.cws.entity.Coordinates;
import uk.co.mruoc.cws.entity.Id;
import uk.co.mruoc.cws.entity.Word;
import uk.co.mruoc.cws.entity.Words;
import uk.co.mruoc.cws.usecase.ImageDownloader;
import uk.co.mruoc.cws.usecase.WordExtractor;

@RequiredArgsConstructor
public class TextractWordExtractor implements WordExtractor {

  private final ImageDownloader downloader;
  private final ImageProcessor processor;
  private final GridDimensionsCalculator calculator;
  private final TextractClient client;

  public TextractWordExtractor(ImageDownloader downloader, TextractClient client) {
    this(downloader, new ImageProcessor(), new GridDimensionsCalculator(), client);
  }

  @Override
  public Words extractWords(String imageUrl) {
    var image = downloader.downloadImage(imageUrl);
    var grid = processor.extractGrid(image);
    var binary = processor.process(image);
    var dimensions = calculator.calculateDimensions(binary).withGrid(grid);
    var processedGrid = dimensions.getProcessedGrid();

    Document document =
        Document.builder().bytes(SdkBytes.fromByteArray(toBytes(processedGrid, ".png"))).build();

    AnalyzeDocumentRequest request =
        AnalyzeDocumentRequest.builder()
            .document(document)
            .featureTypes(FeatureType.TABLES) // analyze tables in the grid
            .build();

    AnalyzeDocumentResponse response = client.analyzeDocument(request);

    List<Block> blocks = response.blocks();

    Collection<Word> words = new ArrayList<>();
    // Iterate over detected cells in tables
    for (Block block : blocks) {
      if (block.blockType() == BlockType.CELL) {
        String text = "";
        if (block.relationships() != null) {
          for (Relationship rel : block.relationships()) {
            if (rel.type() == RelationshipType.CHILD) {
              for (String childId : rel.ids()) {
                Block wordBlock =
                    blocks.stream().filter(b -> b.id().equals(childId)).findFirst().orElse(null);
                if (wordBlock != null && wordBlock.blockType() == BlockType.WORD) {
                  text += wordBlock.text() + " ";
                }
              }
            }
          }
        }
        if (StringUtils.isNotEmpty(text.trim())) {
          System.out.printf(
              "Row: %d, Column: %d, Text: %s%n",
              block.rowIndex(), block.columnIndex(), text.trim());
          words.add(
              Word.builder()
                  .id(new Id(Integer.parseInt(text.trim()), null))
                  .length(-1)
                  .coordinates(new Coordinates(block.columnIndex() - 1, block.rowIndex() - 1))
                  .build());
        }
      }
    }
    return new Words(words);
  }

  public byte[] toBytes(Mat mat, String format) {
    // List to store encoded image
    MatOfByte mob = new MatOfByte();

    // Encode the Mat into the specified format
    boolean success = Imgcodecs.imencode(format, mat, mob);
    if (!success) {
      throw new RuntimeException("Failed to encode Mat to " + format);
    }

    return mob.toArray();
  }
}
