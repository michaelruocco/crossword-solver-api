package uk.co.mruoc.cws.solver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import uk.co.mruoc.cws.entity.Answer;
import uk.co.mruoc.cws.entity.Answers;
import uk.co.mruoc.cws.entity.Cell;
import uk.co.mruoc.cws.entity.Cells;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.entity.Clues;

@RequiredArgsConstructor
public class JsonMapper {

  private final ObjectMapper mapper;

  public JsonMapper() {
    this(buildMapper());
  }

  public String jsonEscape(String json) {
    try {
      return mapper.writeValueAsString(json);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public String extractFirstContentText(String json) {
    try {
      var root = mapper.readTree(json);
      var textNode = root.path("content").get(0).path("text");
      return textNode.asText();
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public Clues toClues(String json) {
    try {
      return new Clues(mapper.readValue(json, Clue[].class));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public Cells toCells(String json) {
    try {
      return new Cells(mapper.readValue(json, Cell[].class));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public Answers toAnswers(String json) {
    try {
      return new Answers(mapper.readValue(json, Answer[].class));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public String toJson(Clues clues) {
    try {
      return mapper.writeValueAsString(clues.stream().toArray());
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  private static ObjectMapper buildMapper() {
    return new ObjectMapper()
        .registerModule(new CrosswordModule())
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }
}
