package uk.co.mruoc.cws.hackathon;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class HackathonResult {
  private int correct;
  private int total;

  public boolean allCorrect() {
    return correct >= total;
  }
}
