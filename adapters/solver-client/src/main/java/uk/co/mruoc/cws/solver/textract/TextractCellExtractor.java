package uk.co.mruoc.cws.solver.textract;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.AnalyzeDocumentRequest;
import software.amazon.awssdk.services.textract.model.Block;
import software.amazon.awssdk.services.textract.model.BlockType;
import software.amazon.awssdk.services.textract.model.BoundingBox;
import software.amazon.awssdk.services.textract.model.Document;
import software.amazon.awssdk.services.textract.model.FeatureType;
import software.amazon.awssdk.services.textract.model.Relationship;
import software.amazon.awssdk.services.textract.model.RelationshipType;
import uk.co.mruoc.cws.entity.Cell;
import uk.co.mruoc.cws.entity.Cells;
import uk.co.mruoc.cws.entity.Coordinates;
import uk.co.mruoc.cws.image.DefaultImageDownloader;
import uk.co.mruoc.cws.usecase.CellExtractor;
import uk.co.mruoc.cws.usecase.ImageDownloader;

@Slf4j
@RequiredArgsConstructor
public class TextractCellExtractor implements CellExtractor {

  private final ImageDownloader downloader;
  private final ProcessedGridImageFactory gridImageFactory;
  private final TextractClient client;

  public TextractCellExtractor(TextractClient client) {
    this(new DefaultImageDownloader(), client);
  }

  public TextractCellExtractor(ImageDownloader imageDownloader, TextractClient client) {
    this(imageDownloader, new ProcessedGridImageFactory(), client);
  }

  @Override
  public Cells extractCells(String imageUrl) {
    var image = downloader.downloadImage(imageUrl);
    return extractCells(image);
  }

  private Cells extractCells(BufferedImage image) {
    var bytes = gridImageFactory.toProcessedGridImageBytes(image);
    return toCells(bytes);
  }

  private Cells toCells(byte[] bytes) {
    var document = Document.builder().bytes(SdkBytes.fromByteArray(bytes)).build();
    var request =
        AnalyzeDocumentRequest.builder()
            .document(document)
            .featureTypes(FeatureType.TABLES)
            .build();
    var response = client.analyzeDocument(request);
    return toCells(response.blocks());
  }

  private Cells toCells(Collection<Block> blocks) {
    List<Block> cellBlocks = blocks.stream().filter(b -> b.blockType() == BlockType.CELL).toList();

    Map<String, Coordinates> coordinates = correctCoordinates(cellBlocks);

    return new Cells(
        cellBlocks.stream()
            .map(block -> toCell(blocks, block, coordinates))
            .flatMap(Optional::stream)
            .sorted(Comparator.comparingInt(Cell::id))
            .toList());
  }

  private Optional<Cell> toCell(
      Collection<Block> blocks, Block block, Map<String, Coordinates> coordinates) {
    var text = findChildText(blocks, block);
    if (StringUtils.isEmpty(text)) {
      return Optional.empty();
    }
    return Optional.of(toCell(block, text, coordinates));
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

  private Cell toCell(Block block, String text, Map<String, Coordinates> coordinates) {
    var cellCoordinates = coordinates.get(block.id());
    var id = Integer.parseInt(text);
    return new Cell(id, cellCoordinates);
  }

  public static Map<String, Coordinates> correctCoordinates(Collection<Block> blocks) {
    var sorted =
        blocks.stream()
            .sorted(Comparator.comparing(b -> b.geometry().boundingBox().top()))
            .toList();

    // 2️⃣ Group into rows
    List<List<Block>> rows = new ArrayList<>();
    List<Block> current = new ArrayList<>();

    for (Block block : sorted) {
      if (current.isEmpty()) {
        current.add(block);
        continue;
      }

      Block firstInRow = current.getFirst();
      BoundingBox firstBox = firstInRow.geometry().boundingBox();
      BoundingBox currentBox = block.geometry().boundingBox();

      boolean sameRow = Math.abs(currentBox.top() - firstBox.top()) < firstBox.height() * 0.6f;

      if (sameRow) {
        current.add(block);
      } else {
        rows.add(new ArrayList<>(current));
        current.clear();
        current.add(block);
      }
    }
    if (!current.isEmpty()) {
      rows.add(current);
    }

    // 3️⃣ Sort each row left-to-right
    for (List<Block> row : rows) {
      row.sort(Comparator.comparing(b -> b.geometry().boundingBox().left()));
    }

    // 4️⃣ Build coordinate map
    Map<String, Coordinates> result = new HashMap<>();
    for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
      List<Block> row = rows.get(rowIndex);
      for (int colIndex = 0; colIndex < row.size(); colIndex++) {
        Block cell = row.get(colIndex);
        result.put(cell.id(), new Coordinates(colIndex, rowIndex));
      }
    }

    return result;
  }
}
