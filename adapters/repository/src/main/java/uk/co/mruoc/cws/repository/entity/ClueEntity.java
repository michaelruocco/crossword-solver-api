package uk.co.mruoc.cws.repository.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.Collection;
import lombok.Data;

@Data
@Entity
@IdClass(ClueEntityId.class)
@Table(name = "clue")
public class ClueEntity {
  @Id private long puzzleId;
  @Id private String clueId;
  private String text;

  @ElementCollection
  @CollectionTable(
      name = "clue_lengths",
      joinColumns = {@JoinColumn(name = "puzzle_id"), @JoinColumn(name = "clue_id")})
  @Column(name = "length")
  private Collection<Integer> lengths;
}
