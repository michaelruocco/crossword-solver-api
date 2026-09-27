package uk.co.mruoc.cws.repository.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Optional;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@IdClass(CellEntityId.class)
@Table(name = "cell")
public class CellEntity {
  @Id private UUID puzzleId;
  @Id private int x;
  @Id private int y;
  private Integer cellId;
  private boolean black;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "puzzleId", referencedColumnName = "id", insertable = false, updatable = false)
  private PuzzleEntity puzzle;

  public Optional<Integer> getCellId() {
    return Optional.ofNullable(cellId);
  }
}
