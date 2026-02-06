package uk.co.mruoc.cws.repository.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "candidate_clue_answer")
public class CandidateAnswerEntity {
  @Id private UUID id;

  private String value;
  private int score;
  private String clueId;
}
