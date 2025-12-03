package uk.co.mruoc.cws.image;

public class ImageException extends RuntimeException {

  public ImageException(String message) {
    super(message);
  }

  public ImageException(Throwable cause) {
    super(cause);
  }
}
