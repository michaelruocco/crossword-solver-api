package uk.co.mruoc.cws.repository.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.util.Collection;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import uk.co.mruoc.cws.entity.ClueType;

@Getter
@Setter
@Entity
@IdClass(ClueEntityId.class)
@Table(name = "clue")
public class ClueEntity {
  @Id private UUID puzzleId;
  @Id private String clueId;
  private String text;

  @Enumerated(EnumType.STRING)
  private ClueType type;

  @ElementCollection
  @CollectionTable(
      name = "clue_lengths",
      joinColumns = {
        @JoinColumn(name = "puzzle_id", referencedColumnName = "puzzleId"),
        @JoinColumn(name = "clue_id", referencedColumnName = "clueId")
      })
  @Column(name = "length")
  private Collection<Integer> lengths;
}
