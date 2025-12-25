package uk.co.mruoc.cws.repository.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Collection;
import lombok.Data;

@Data
@Entity
@Table(name = "candidate_clue")
public class CandidateClueEntity {
  @Id private String clueId;
  private String text;
  private String pattern;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "clueId")
  private Collection<CandidateAnswerEntity> answers;
}
