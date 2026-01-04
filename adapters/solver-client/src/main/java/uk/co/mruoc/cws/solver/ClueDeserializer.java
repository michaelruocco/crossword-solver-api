package uk.co.mruoc.cws.solver;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.util.Collection;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.entity.Direction;
import uk.co.mruoc.cws.entity.Id;

public class ClueDeserializer extends StdDeserializer<Clue> {

  private static final TypeReference<Collection<Integer>> INT_COLLECTION = new TypeReference<>() {
        // intentionally blank
      };

  protected ClueDeserializer() {
    super(Clue.class);
  }

  @Override
  public Clue deserialize(JsonParser parser, DeserializationContext context) throws IOException {
    JsonNode node = parser.getCodec().readTree(parser);
    return Clue.builder()
        .id(new Id(node.get("id").intValue(), Direction.valueOf(node.get("direction").asText())))
        .text(node.get("text").asText())
        .lengths(node.get("lengths").traverse(parser.getCodec()).readValueAs(INT_COLLECTION))
        .build();
  }
}
