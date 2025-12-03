package uk.co.mruoc.cws.api;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class ApiCreatePuzzleRequest {
  private String imageUrl;
}
