package uk.co.mruoc.cws.usecase;

public class FilenameConverter {

  public String removeExtension(String filename) {
    return filename.substring(0, filename.lastIndexOf('.'));
  }
}
