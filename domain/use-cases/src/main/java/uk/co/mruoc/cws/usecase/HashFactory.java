package uk.co.mruoc.cws.usecase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashFactory {

  public String toHash(byte[] bytes) {
    try {
      var digest = MessageDigest.getInstance("SHA-256");
      var hashBytes = digest.digest(bytes);
      return toString(hashBytes);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalArgumentException(e);
    }
  }

  private String toString(byte[] bytes) {
    var hash = new StringBuilder();
    for (byte b : bytes) {
      hash.append(String.format("%02x", b));
    }
    return hash.toString();
  }
}
