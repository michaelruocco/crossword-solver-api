package uk.co.mruoc.cws.solver.textract;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import software.amazon.awssdk.services.textract.model.Block;
import software.amazon.awssdk.services.textract.model.BlockType;
import software.amazon.awssdk.services.textract.model.BoundingBox;
import software.amazon.awssdk.services.textract.model.Relationship;
import software.amazon.awssdk.services.textract.model.RelationshipType;
import uk.co.mruoc.cws.entity.Cell;
import uk.co.mruoc.cws.entity.Cells;
import uk.co.mruoc.cws.entity.Coordinates;

@RequiredArgsConstructor
public class BlockConverter {

  public Cells toCells(Collection<Block> blocks) {
    var cellBlocks = blocks.stream().filter(b -> b.blockType() == BlockType.CELL).toList();
    var coordinates = correctCoordinates(cellBlocks);
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

  private Map<String, Coordinates> correctCoordinates(Collection<Block> blocks) {
    var rows = toRows(blocks);
    return toCoordinateMap(rows);
  }

  private List<Row> toRows(Collection<Block> blocks) {
    var rows = new ArrayList<Row>();
    var current = new Row();
    var sorted = blocks.stream().sorted(topToBottom()).toList();
    for (Block block : sorted) {
      if (current.isEmpty()) {
        current.add(block);
      } else if (current.shouldContain(block)) {
        current.add(block);
      } else {
        rows.add(current);
        current = new Row(block);
      }
    }
    if (!current.isEmpty()) {
      rows.add(current);
    }
    return rows;
  }

  private Comparator<Block> topToBottom() {
    return Comparator.comparing(b -> b.geometry().boundingBox().top());
  }

  private Map<String, Coordinates> toCoordinateMap(List<Row> rows) {
    Map<String, Coordinates> result = new HashMap<>();
    for (int y = 0; y < rows.size(); y++) {
      List<Block> row = rows.get(y).getBlocks();
      for (int x = 0; x < row.size(); x++) {
        Block cell = row.get(x);
        result.put(cell.id(), new Coordinates(x, y));
      }
    }
    return result;
  }

  @RequiredArgsConstructor
  private static class Row {

    private final List<Block> blocks;

    public Row() {
      this(new ArrayList<>());
    }

    public Row(Block block) {
      this(new ArrayList<>(List.of(block)));
    }

    public Block getFirst() {
      return blocks.getFirst();
    }

    public boolean isEmpty() {
      return blocks.isEmpty();
    }

    public void add(Block block) {
      blocks.add(block);
    }

    public List<Block> getBlocks() {
      return blocks.stream().sorted(leftToRight()).toList();
    }

    public boolean shouldContain(Block otherBlock) {
      BoundingBox firstBox = getFirst().geometry().boundingBox();
      BoundingBox currentBox = otherBlock.geometry().boundingBox();
      return Math.abs(currentBox.top() - firstBox.top()) < firstBox.height() * 0.6f;
    }

    private Comparator<Block> leftToRight() {
      return Comparator.comparing(b -> b.geometry().boundingBox().left());
    }
  }
}
