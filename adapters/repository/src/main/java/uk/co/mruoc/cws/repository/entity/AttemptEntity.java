package uk.co.mruoc.cws.repository.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Collection;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "attempt")
public class AttemptEntity {
  @Id private UUID id;

  @ManyToOne
  @JoinColumn(name = "puzzleId")
  private PuzzleEntity puzzle;

  @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL, orphanRemoval = true)
  private Collection<AttemptAnswerEntity> answers;

  public void setAnswers(Collection<AttemptAnswerEntity> answers) {
    this.answers = answers;
    answers.forEach(answer -> answer.setAttempt(this));
  }
}
