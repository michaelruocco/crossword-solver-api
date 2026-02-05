package uk.co.mruoc.cws.solver;

import lombok.RequiredArgsConstructor;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;
import uk.co.mruoc.cws.entity.Answer;
import uk.co.mruoc.cws.entity.Answers;
import uk.co.mruoc.cws.entity.Cell;
import uk.co.mruoc.cws.entity.Cells;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.entity.Clues;

@RequiredArgsConstructor
public class CrosswordJsonMapper {

  private final JsonMapper mapper;

  public CrosswordJsonMapper() {
    this(buildMapper());
  }

  public String jsonEscape(String json) {
    try {
      return mapper.writeValueAsString(json);
    } catch (JacksonException e) {
      throw new RuntimeException(e);
    }
  }

  public String extractFirstContentText(String json) {
    try {
      var root = mapper.readTree(json);
      var textNode = root.path("content").get(0).path("text");
      return textNode.stringValue();
    } catch (JacksonException e) {
      throw new RuntimeException(e);
    }
  }

  public Clues toClues(String json) {
    try {
      return new Clues(mapper.readValue(json, Clue[].class));
    } catch (JacksonException e) {
      throw new RuntimeException(e);
    }
  }

  public Cells toCells(String json) {
    try {
      return new Cells(mapper.readValue(json, Cell[].class));
    } catch (JacksonException e) {
      throw new RuntimeException(e);
    }
  }

  public Answers toAnswers(String json) {
    try {
      return new Answers(mapper.readValue(json, Answer[].class));
    } catch (JacksonException e) {
      throw new RuntimeException(e);
    }
  }

  private static JsonMapper buildMapper() {
    return JsonMapper.builder()
        .addModule(new CrosswordModule())
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .build();
  }
}
