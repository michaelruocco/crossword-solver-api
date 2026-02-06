package uk.co.mruoc.cws.repository.entity;

import java.io.Serializable;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class CellEntityId implements Serializable {
  private UUID puzzleId;
  private int x;
  private int y;
}
