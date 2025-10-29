package uk.co.mruoc.cws.usecase;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IncrementingIdSupplier implements Supplier<Long> {

  private final AtomicLong nextId;

  public IncrementingIdSupplier() {
    this(new AtomicLong(1));
  }

  @Override
  public Long get() {
    return nextId.getAndIncrement();
  }
}
