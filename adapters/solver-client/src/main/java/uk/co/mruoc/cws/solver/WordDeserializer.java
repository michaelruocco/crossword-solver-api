package uk.co.mruoc.cws.solver;

import java.util.Optional;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.deser.std.StdDeserializer;
import uk.co.mruoc.cws.entity.Coordinates;
import uk.co.mruoc.cws.entity.Direction;
import uk.co.mruoc.cws.entity.Id;
import uk.co.mruoc.cws.entity.Word;

public class WordDeserializer extends StdDeserializer<Word> {

  protected WordDeserializer() {
    super(Word.class);
  }

  @Override
  public Word deserialize(JsonParser parser, DeserializationContext context) {
    JsonNode node = context.readTree(parser);
    return Word.builder()
        .id(new Id(node.get("id").intValue(), toDirectionIfPresent(node)))
        .length(toLengthIfPresent(node))
        .coordinates(context.readTreeAsValue(node.get("coordinates"), Coordinates.class))
        .build();
  }

  private static Direction toDirectionIfPresent(JsonNode node) {
    return Optional.ofNullable(node.get("direction"))
        .map(directionNode -> Direction.valueOf(directionNode.stringValue()))
        .orElse(null);
  }

  private static int toLengthIfPresent(JsonNode node) {
    return Optional.ofNullable(node.get("length")).map(JsonNode::intValue).orElse(-1);
  }
}
