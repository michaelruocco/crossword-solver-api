package uk.co.mruoc.cws.solver.stub;

import lombok.RequiredArgsConstructor;
import uk.co.mruoc.cws.entity.Answer;
import uk.co.mruoc.cws.entity.Answers;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.entity.Clues;
import uk.co.mruoc.cws.solver.JsonMapper;
import uk.co.mruoc.cws.usecase.AnswerFinder;
import uk.co.mruoc.file.FileLoader;

@RequiredArgsConstructor
public class StubAnswerFinder implements AnswerFinder {

  private final Answers answers;

  public StubAnswerFinder(String answerJsonPath) {
    this(answerJsonPath, new JsonMapper());
  }

  public StubAnswerFinder(String answerJsonPath, JsonMapper mapper) {
    this(toAnswers(answerJsonPath, mapper));
  }

  @Override
  public Answers findAnswers(Clues clues) {
    return new Answers(clues.stream().map(this::findAnswer).toList());
  }

  @Override
  public Answer findAnswer(Clue clue) {
    return answers.findById(clue.id()).orElse(Answer.noMatch(clue));
  }

  private static Answers toAnswers(String answersJsonPath, JsonMapper mapper) {
    var json = FileLoader.loadContentFromClasspath(answersJsonPath);
    return mapper.toAnswers(json);
  }
}
