package uk.co.mruoc.cws.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class AnswersTest {

  @Test
  void shouldRemoveAnyAnswersThatAreNotTheSameInBothSets() {
    var answer1 = builder().id(Id.across(1)).value("value-1").build();
    var answer2 = builder().id(Id.down(2)).value("value-2").build();
    var answer3 = builder().id(Id.across(3)).value("value-3").build();
    var answers1 = new Answers(answer1, answer2, answer3);
    var answers2 = new Answers(answer1, answer2.withValue("value-4"), answer3.withId(Id.down(4)));

    var sameAnswers = answers1.removeDifferent(answers2);

    assertThat(sameAnswers).containsExactly(answer1);
  }

  private static Answer.AnswerBuilder builder() {
    return Answer.builder().confidenceScore(100).confirmed(false);
  }
}
