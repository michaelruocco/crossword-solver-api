package uk.co.mruoc.cws.solver.textract;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.AnalyzeDocumentRequest;
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
    return toWords(processedGrid);
  }

  private Words toWords(Mat grid) {
    var document = Document.builder().bytes(SdkBytes.fromByteArray(toBytes(grid, ".png"))).build();
    var request =
        AnalyzeDocumentRequest.builder()
            .document(document)
            .featureTypes(FeatureType.TABLES)
            .build();
    var response = client.analyzeDocument(request);
    return toWords(response.blocks());
  }

  private Words toWords(Collection<Block> blocks) {
    return new Words(
        blocks.stream()
            .filter(block -> block.blockType() == BlockType.CELL)
            .map(block -> toWord(blocks, block))
            .flatMap(Optional::stream)
            .toList());
  }

  private Optional<Word> toWord(Collection<Block> blocks, Block block) {
    var text = findChildText(blocks, block);
    if (StringUtils.isEmpty(text)) {
      return Optional.empty();
    }
    return Optional.of(toWord(block, text));
  }

  private String findChildText(Collection<Block> blocks, Block block) {
    return block.relationships().stream()
        .filter(relationship -> relationship.type() == RelationshipType.CHILD)
        .map(Relationship::ids)
        .flatMap(Collection::stream)
        .map(childId -> findChildText(blocks, childId))
        .filter(StringUtils::isNotEmpty)
        .collect(Collectors.joining(" "));
  }

  private String findChildText(Collection<Block> blocks, String childId) {
    return blocks.stream()
        .filter(b -> b.id().equals(childId))
        .filter(b -> b.blockType() == BlockType.WORD)
        .map(Block::text)
        .collect(Collectors.joining(" "));
  }

  private Word toWord(Block block, String text) {
    return Word.builder()
        .id(new Id(Integer.parseInt(text), null))
        .length(-1)
        .coordinates(new Coordinates(block.columnIndex() - 1, block.rowIndex() - 1))
        .build();
  }

  public byte[] toBytes(Mat mat, String format) {
    MatOfByte mob = new MatOfByte();
    boolean success = Imgcodecs.imencode(format, mat, mob);
    if (!success) {
      throw new RuntimeException("Failed to encode Mat to " + format);
    }
    return mob.toArray();
  }
}
