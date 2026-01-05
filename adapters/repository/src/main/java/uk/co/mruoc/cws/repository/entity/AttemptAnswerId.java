package uk.co.mruoc.cws.repository.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class AttemptAnswerId implements Serializable {
  private long attempt;
  private String clueId;

}
