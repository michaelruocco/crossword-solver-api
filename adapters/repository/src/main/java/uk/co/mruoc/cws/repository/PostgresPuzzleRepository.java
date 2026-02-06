package uk.co.mruoc.cws.repository;

import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import uk.co.mruoc.cws.entity.Puzzle;
import uk.co.mruoc.cws.usecase.puzzle.PuzzleRepository;

@RequiredArgsConstructor
public class PostgresPuzzleRepository implements PuzzleRepository {

  private final PostgresJpaPuzzleRepository jpaRepository;
  private final PuzzleEntityConverter entityConverter;

  public PostgresPuzzleRepository(PostgresJpaPuzzleRepository jpaRepository) {
    this(jpaRepository, new PuzzleEntityConverter());
  }

  @Transactional(readOnly = true)
  @Override
  public Optional<Puzzle> findById(UUID id) {
    return jpaRepository.findById(id).map(entityConverter::toPuzzle);
  }

  @Transactional(readOnly = true)
  @Override
  public Optional<Puzzle> findByHash(String hash) {
    return jpaRepository.findByHash(hash).map(entityConverter::toPuzzle);
  }

  @Override
  public void save(Puzzle puzzle) {
    jpaRepository.save(entityConverter.toEntity(puzzle));
  }
}
