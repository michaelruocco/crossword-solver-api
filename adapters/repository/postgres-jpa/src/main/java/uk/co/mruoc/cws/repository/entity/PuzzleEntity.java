package uk.co.mruoc.cws.repository.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.Collection;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;

@Getter
@Setter
@Entity
@Table(
    name = "puzzle",
    uniqueConstraints =
        @UniqueConstraint(
            name = "unique_puzzle_hash",
            columnNames = {"hash"}))
public class PuzzleEntity {

  @Id private UUID id;
  private String name;
  private String format;

  @Column(unique = true)
  private String hash;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "puzzleId")
  private Collection<ClueEntity> clues;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "puzzleId")
  private Collection<CellEntity> cells;

  private Instant createdAt;

  @Formula("(select count(a.id) from attempt a where a.puzzle_id = id)")
  private long attemptCount;

  private int columnWidth;
  private int rowHeight;
}
