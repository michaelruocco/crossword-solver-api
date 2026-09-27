package uk.co.mruoc.cws.solver.stub;

import lombok.RequiredArgsConstructor;
import uk.co.mruoc.cws.entity.Answer;
import uk.co.mruoc.cws.entity.Answers;
import uk.co.mruoc.cws.entity.Candidates;
import uk.co.mruoc.cws.entity.Clue;
import uk.co.mruoc.cws.solver.CrosswordJsonMapper;
import uk.co.mruoc.cws.usecase.AnswerFinder;
import uk.co.mruoc.file.FileLoader;

@RequiredArgsConstructor
public class StubAnswerFinder implements AnswerFinder {

  private final Answers answers;

  public StubAnswerFinder(String answerJsonPath) {
    this(answerJsonPath, new CrosswordJsonMapper());
  }

  public StubAnswerFinder(String answerJsonPath, CrosswordJsonMapper mapper) {
    this(toAnswers(answerJsonPath, mapper));
  }

  @Override
  public Candidates findCandidates(Clue clue, int numberOfCandidates) {
    return new Candidates(clue, findAnswer(clue));
  }

  private Answer findAnswer(Clue clue) {
    return answers.findById(clue.id()).orElse(Answer.noMatch(clue));
  }

  private static Answers toAnswers(String answersJsonPath, CrosswordJsonMapper mapper) {
    var json = FileLoader.loadContentFromClasspath(answersJsonPath);
    return mapper.toAnswers(json);
  }
}
