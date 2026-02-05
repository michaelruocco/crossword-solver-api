package uk.co.mruoc.cws.solver;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.deser.std.StdDeserializer;
import uk.co.mruoc.cws.entity.Answer;
import uk.co.mruoc.cws.entity.Direction;
import uk.co.mruoc.cws.entity.Id;

public class AnswerDeserializer extends StdDeserializer<Answer> {

  protected AnswerDeserializer() {
    super(Answer.class);
  }

  @Override
  public Answer deserialize(JsonParser parser, DeserializationContext context) {
    JsonNode node = context.readTree(parser);
    return Answer.builder()
        .id(
            new Id(
                node.get("id").intValue(), Direction.valueOf(node.get("direction").stringValue())))
        .value(node.get("value").stringValue())
        .confidenceScore(node.get("confidenceScore").intValue())
        .confirmed(node.get("confirmed").asBoolean())
        .build();
  }
}
