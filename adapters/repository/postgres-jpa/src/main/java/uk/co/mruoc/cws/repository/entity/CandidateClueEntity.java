package uk.co.mruoc.cws.repository.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Collection;
import lombok.Getter;
import lombok.Setter;
import uk.co.mruoc.cws.entity.ClueType;

@Getter
@Setter
@Entity
@Table(name = "candidate_clue")
public class CandidateClueEntity {
  @Id private String clueId;
  private String text;
  @Enumerated(EnumType.STRING)
  private ClueType type;
  private String pattern;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "clueId")
  private Collection<CandidateAnswerEntity> answers;
}
