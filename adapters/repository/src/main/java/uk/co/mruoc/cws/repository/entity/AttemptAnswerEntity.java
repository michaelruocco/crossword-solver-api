package uk.co.mruoc.cws.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@IdClass(AttemptAnswerId.class)
@Table(
    name = "attempt_answer",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "attempt_clue_unique",
          columnNames = {"attempt_id", "clue_id"})
    })
public class AttemptAnswerEntity {
  @Id
  @ManyToOne(optional = false)
  @JoinColumn(name = "attempt_id")
  private AttemptEntity attempt;

  @Id
  @Column(name = "clue_id", nullable = false)
  private String clueId;
  private String value;
  private int score;
  private boolean confirmed;
}
