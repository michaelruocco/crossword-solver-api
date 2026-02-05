package uk.co.mruoc.cws.solver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.deser.std.StdDeserializer;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.entity.Direction;
import uk.co.mruoc.cws.entity.Id;

public class ClueDeserializer extends StdDeserializer<Clue> {

  protected ClueDeserializer() {
    super(Clue.class);
  }

  @Override
  public Clue deserialize(JsonParser parser, DeserializationContext context) {
    JsonNode node = context.readTree(parser);
    return Clue.builder()
        .id(
            new Id(
                node.get("id").intValue(), Direction.valueOf(node.get("direction").stringValue())))
        .text(node.get("text").stringValue())
        .lengths(toLengths(node.get("lengths")))
        .build();
  }

  private static Collection<Integer> toLengths(JsonNode node) {
    if (node == null || !node.isArray()) {
      return Collections.emptyList();
    }
    Collection<Integer> lengths = new ArrayList<>(node.size());
    for (JsonNode element : node) {
      lengths.add(element.intValue());
    }
    return lengths;
  }
}
