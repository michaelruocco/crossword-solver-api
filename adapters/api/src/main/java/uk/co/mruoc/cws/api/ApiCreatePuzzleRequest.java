package uk.co.mruoc.cws.api;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@NoArgsConstructor(force = true)
@Data
public class ApiCreatePuzzleRequest {
  private final String imageUrl;
}
