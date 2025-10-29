package uk.co.mruoc.cws.solver;

import com.fasterxml.jackson.databind.module.SimpleModule;
import uk.co.mruoc.cws.entity.Answer;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.entity.Word;

public class CrosswordModule extends SimpleModule {

  public CrosswordModule() {
    addDeserializer(Clue.class, new ClueDeserializer());
    addDeserializer(Answer.class, new AnswerDeserializer());
    addDeserializer(Word.class, new WordDeserializer());
  }
}
