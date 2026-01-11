package uk.co.mruoc.cws.entity;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Result {

    private final Attempt attempt;
    private final int totalCount;
    private final int correctCount;
    private final Answers incorrectAnswers;

    public double percentageCorrect() {
        return (correctCount / (double) totalCount) * 100;
    }

    public boolean hasIncorrectAnswers() {
        return correctCount < totalCount;
    }
}
