package uk.co.mruoc.cws.usecase;

import java.util.UUID;
import java.util.function.Supplier;

public class UUIDSupplier implements Supplier<UUID> {

  @Override
  public UUID get() {
    return UUID.randomUUID();
  }
}
