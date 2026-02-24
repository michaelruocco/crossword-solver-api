package uk.co.mruoc.cws.repository.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Collection;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Formula;

@Getter
@Setter
@Entity
@Table(name = "attempt")
public class AttemptEntity {
  @Id private UUID id;
  private Instant createdAt;
  private boolean solving;

  @ManyToOne
  @JoinColumn(name = "puzzleId")
  private PuzzleEntity puzzle;

  @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL, orphanRemoval = true)
  private Collection<AttemptAnswerEntity> answers;

  @Formula("(select count(aa.clue_id) from attempt_answer aa where aa.attempt_id = id)")
  private long answerCount;

  @Formula("(select count(c.clue_id) from clue c where c.puzzle_id = puzzle_id)")
  private long clueCount;

  public void setAnswers(Collection<AttemptAnswerEntity> answers) {
    this.answers = answers;
    answers.forEach(answer -> answer.setAttempt(this));
  }
}
