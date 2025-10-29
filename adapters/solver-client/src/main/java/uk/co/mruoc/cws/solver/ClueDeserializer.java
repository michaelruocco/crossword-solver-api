package uk.co.mruoc.cws.solver;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.util.Collection;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.entity.Direction;
import uk.co.mruoc.cws.entity.Id;
import uk.co.mruoc.json.jackson.JsonNodeConverter;
import uk.co.mruoc.json.jackson.JsonParserConverter;

public class ClueDeserializer extends StdDeserializer<Clue> {

  private static final TypeReference<Collection<Integer>> INT_COLLECTION = new TypeReference<>() {
        // intentionally blank
      };

  protected ClueDeserializer() {
    super(Clue.class);
  }

  @Override
  public Clue deserialize(JsonParser parser, DeserializationContext context) {
    JsonNode node = JsonParserConverter.toNode(parser);
    return Clue.builder()
        .id(new Id(node.get("id").intValue(), Direction.valueOf(node.get("direction").asText())))
        .text(node.get("text").asText())
        .lengths(JsonNodeConverter.toCollection(node.get("lengths"), parser, INT_COLLECTION))
        .build();
  }
}
