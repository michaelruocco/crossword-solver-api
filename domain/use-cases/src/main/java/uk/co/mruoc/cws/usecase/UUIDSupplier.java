package uk.co.mruoc.cws.usecase;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;

public class UUIDSupplier implements Supplier<UUID> {

  @Override
  public UUID get() {
    return UUID.randomUUID();
  }
}
