package uk.co.mruoc.cws.repository;

import java.util.Collection;
import java.util.Comparator;
import java.util.UUID;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import uk.co.mruoc.cws.entity.Attempt;
import uk.co.mruoc.cws.entity.AttemptSummary;
import uk.co.mruoc.cws.usecase.attempt.AttemptRepository;
import uk.co.mruoc.cws.usecase.attempt.AttemptSummaryRepository;

@Builder
@Slf4j
public class StubAttemptSummaryRepository implements AttemptSummaryRepository {

  private final AttemptRepository attemptRepository;
  private final StubRepositoryPuzzleConverter puzzleConverter;

  @Override
  public Collection<AttemptSummary> findSummariesByPuzzleId(UUID puzzleId) {
    return attemptRepository.findAll().stream()
        .filter(attempt -> attempt.puzzleId().equals(puzzleId))
        .map(this::toSummary)
        .sorted(Comparator.comparing(AttemptSummary::getCreatedAt).reversed())
        .toList();
  }

  private AttemptSummary toSummary(Attempt attempt) {
    return AttemptSummary.builder()
        .id(attempt.id())
        .createdAt(attempt.createdAt())
        .clueCount(attempt.puzzle().clueCount())
        .answerCount(attempt.getConfirmedAnswerCount())
        .solving(attempt.solving())
        .build();
  }
}
