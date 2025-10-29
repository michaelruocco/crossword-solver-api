package uk.co.mruoc.cws.solver;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import uk.co.mruoc.cws.entity.Answer;
import uk.co.mruoc.cws.entity.Direction;
import uk.co.mruoc.cws.entity.Id;
import uk.co.mruoc.json.jackson.JsonParserConverter;

public class AnswerDeserializer extends StdDeserializer<Answer> {

  protected AnswerDeserializer() {
    super(Answer.class);
  }

  @Override
  public Answer deserialize(JsonParser parser, DeserializationContext context) {
    JsonNode node = JsonParserConverter.toNode(parser);
    return Answer.builder()
        .id(new Id(node.get("id").intValue(), Direction.valueOf(node.get("direction").asText())))
        .value(node.get("value").asText())
        .confidenceScore(node.get("confidenceScore").intValue())
        .confirmed(node.get("confirmed").asBoolean())
        .build();
  }
}
