package uk.co.mruoc.cws.solver.stub;

import java.util.HashMap;
import java.util.Map;
import uk.co.mruoc.cws.entity.Answer;
import uk.co.mruoc.cws.entity.Id;

public class Puzzle1FakeAnswers extends FakeAnswers {

  public Puzzle1FakeAnswers() {
    super(buildAnswers());
  }

  private static Map<String, Answer> buildAnswers() {
    var map = new HashMap<String, Answer>();
    addAnswer(map, "?????", new Answer("14A", "TESLA", 100));
    addAnswer(map, "????S????????", new Answer("4D", "IDIOSYNCRATIC", 98));
    addAnswer(map, "??D??", new Answer("8A", "VODKA", 98));
    addAnswer(map, "??????N??????", new Answer("18A", "LAYDOWNTHELAW", 98));
    addAnswer(map, "T?O??", new Answer("14D", "TROUT", 98));
    addAnswer(map, "T?R??", new Answer("21A", "TURNS", 95));
    addAnswer(map, "A?H?S", new Answer("15D", "ATHOS", 95));
    addAnswer(map, "??I??", new Answer("27A", "NOISY", 98));
    addAnswer(map, "?V?", new Answer("3D", "IVY", 98));
    addAnswer(map, "???I", new Answer("1A", "FIJI", 98));
    addAnswer(map, "????Y", new Answer("10A", "SOGGY", 98));
    addAnswer(map, "I?G??", new Answer("2D", "INGOT", 95));
    addAnswer(map, "??T", new Answer("12A", "OPT", 98));
    addAnswer(map, "?S?O", new Answer("7D", "OSLO", 98));
    addAnswer(map, "P?A??", new Answer("13D", "PLAZA", 95));
    addAnswer(map, "??A??", new Answer("17D", "AWARE", 98));
    addAnswer(map, "?A?", new Answer("16A", "TAG", 98));
    addAnswer(map, "?A?", new Answer("5D", "JAB", 99));
    addAnswer(map, "J???", new Answer("5A", "JERK", 98));
    addAnswer(map, "R???T", new Answer("6D", "RIGHT", 95));
    addAnswer(map, "B?G??", new Answer("11A", "BUGLE", 95));
    addAnswer(map, "?E?G", new Answer("9D", "BERG", 95));
    addAnswer(map, "?N?", new Answer("25D", "ENO", 98));
    addAnswer(map, "?A?", new Answer("19A", "SAM", 98));
    addAnswer(map, "?E?", new Answer("22A", "LEE", 98));
    addAnswer(map, "????E", new Answer("24A", "UNITE", 98));
    addAnswer(map, "S?U?", new Answer("19D", "SMUG", 98));
    addAnswer(map, "M?I??", new Answer("20D", "MAIZE", 95));
    addAnswer(map, "?E?O", new Answer("28A", "DEMO", 90));
    addAnswer(map, "L????", new Answer("22D", "LILLE", 98));
    addAnswer(map, "??L??", new Answer("26A", "RELIC", 98));
    addAnswer(map, "RY?", new Answer("26D", "RYE", 95));
    addAnswer(map, "E?E?", new Answer("29A", "EWER", 95));
    addAnswer(map, "E?C?", Answer.noMatchBuilder().id(new Id("23D")).build());
    addAnswer(map, "E???", Answer.noMatchBuilder().id(new Id("23D")).build());
    addAnswer(map, "??C?", new Answer("23D", "SECT", 95));
    return map;
  }
}
