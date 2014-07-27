package org.zezutom.concurrencypatterns;

import org.zezutom.concurrencypatterns.Counter;

/**
 * @author Tomas Zezula
 *
 * Implements the org.zezutom.concurrencypatterns.Counter in a way that is NOT suitable
 * for multi-threaded scenarios.
 */
public class ThreadUnsafeCounter implements Counter {

    private long value;

    public ThreadUnsafeCounter(long value) {
        this.value = value;
    }

    @Override
    public long get() {
        return value;
    }

    @Override
    public long incrementAndGet() {
        return ++value;
    }

    @Override
    public long getAndIncrement() {
        return value++;
    }

    @Override
    public long decrementAndGet() {
        return --value;
    }

    @Override
    public long getAndDecrement() {
        return value--;
    }
}
