package uk.co.mruoc.cws.repository.entity;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class AttemptAnswerId implements Serializable {
  private long attempt;
  private String clueId;
}
