package uk.co.mruoc.cws.entity;

import java.util.Collection;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class CandidatesScore {

  private final int bestScore;
  private final Collection<Integer> alternativeScores;
  private final int numberOfCandidates;
  private final int knownLetterCount;
  private final int totalLetterCount;

  public double averageAlternativeScore() {
    return alternativeScores.stream().mapToDouble(d -> d).average().orElse(0);
  }

  public double confidenceDistributionScore() {
    return (bestScore - averageAlternativeScore()) / 100.0;
  }

  public double countScore() {
    return 1.0 / Math.sqrt(numberOfCandidates);
  }

  public double patternSpecificityScore() {
    return knownLetterCount / (double) totalLetterCount;
  }

  public double evidenceScore() {
    return 0.4 * confidenceDistributionScore()
        + 0.3 * countScore()
        + 0.3 * patternSpecificityScore();
  }

  public double bestConfidenceScore() {
    return bestScore / 100.0;
  }

  public double overallScore() {
    return bestConfidenceScore() * evidenceScore();
  }

  public String asString() {
    return String.format(
        """
                bestScore %d
                alternativeScores %s
                numberOfCandidates %d
                knownLetterCount %d
                totalLetterCount %d
                averageAlternativeScore %.5f
                confidenceDistributionScore %.5f
                countScore %.5f
                patternSpecificityScore %.5f
                bestConfidenceScore %.5f
                evidenceScore %.5f
                overallScore %.5f
                """,
        bestScore,
        alternativeScores.stream().map(Object::toString).collect(Collectors.joining(", ")),
        numberOfCandidates,
        knownLetterCount,
        totalLetterCount,
        averageAlternativeScore(),
        confidenceDistributionScore(),
        countScore(),
        patternSpecificityScore(),
        bestConfidenceScore(),
        evidenceScore(),
        overallScore());
  }
}
