package uk.co.mruoc.cws.repository.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Collection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "puzzle")
public class PuzzleEntity {

  @Id private long id;
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
}
