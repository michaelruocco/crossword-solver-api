package uk.co.mruoc.cws.repository.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@IdClass(CellEntityId.class)
@Table(name = "cell")
public class CellEntity {
  @Id private long puzzleId;
  @Id private int cellId;
  private int x;
  private int y;
}
