package uk.co.mruoc.cws.usecase;

import uk.co.mruoc.cws.entity.Words;

public interface WordExtractor {

  Words extractWords(String imageUrl);
}
