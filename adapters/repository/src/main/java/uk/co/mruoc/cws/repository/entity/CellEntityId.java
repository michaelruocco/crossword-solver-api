package uk.co.mruoc.cws.repository.entity;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class CellEntityId implements Serializable {
  private long puzzleId;
  private int cellId;
}
