package uk.co.mruoc.cws.usecase;

import java.net.URI;

public class UrlConverter {

  public String toFilenameExcludingExtension(String value) {
    var filename = toFilename(value);
    return filename.substring(0, filename.lastIndexOf('.'));
  }

  public String toExtension(String value) {
    var filename = toFilename(value);
    return filename.substring(filename.lastIndexOf('.'));
  }

  public String toFilename(String value) {
    var uri = URI.create(value);
    var path = uri.getPath();
    return path.substring(path.lastIndexOf('/') + 1);
  }
}
