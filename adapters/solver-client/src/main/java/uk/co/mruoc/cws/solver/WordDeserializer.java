package uk.co.mruoc.cws.solver;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.util.Optional;
import uk.co.mruoc.cws.entity.Coordinates;
import uk.co.mruoc.cws.entity.Direction;
import uk.co.mruoc.cws.entity.Id;
import uk.co.mruoc.cws.entity.Word;

public class WordDeserializer extends StdDeserializer<Word> {

  protected WordDeserializer() {
    super(Word.class);
  }

  @Override
  public Word deserialize(JsonParser parser, DeserializationContext context) throws IOException {
    JsonNode node = parser.getCodec().readTree(parser);
    return Word.builder()
        .id(new Id(node.get("id").intValue(), toDirectionIfPresent(node)))
        .length(toLengthIfPresent(node))
        .coordinates(
            node.get("coordinates").traverse(parser.getCodec()).readValueAs(Coordinates.class))
        .build();
  }

  private static Direction toDirectionIfPresent(JsonNode node) {
    return Optional.ofNullable(node.get("direction"))
        .map(directionNode -> Direction.valueOf(directionNode.asText()))
        .orElse(null);
  }

  private static int toLengthIfPresent(JsonNode node) {
    return Optional.ofNullable(node.get("length")).map(JsonNode::intValue).orElse(-1);
  }
}
