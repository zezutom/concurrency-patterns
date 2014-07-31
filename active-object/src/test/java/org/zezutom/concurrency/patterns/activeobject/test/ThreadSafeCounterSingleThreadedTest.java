package org.zezutom.concurrency.patterns.activeobject.test;

import org.junit.Before;
import org.junit.Test;
import org.zezutom.concurrencypatterns.activeobject.Counter;
import org.zezutom.concurrencypatterns.activeobject.ThreadSafeCounter;

import static org.junit.Assert.assertEquals;

/**
 * @author Tomas Zezula
 *
 * Proves the core functionality, all tests should pass.
 */
public class ThreadSafeCounterSingleThreadedTest {

    // The value the counter is initialized with
    public static final long INITIAL_VALUE = 10L;

    private Counter counter;

    @Before
    public void init() {
        counter = new ThreadSafeCounter(INITIAL_VALUE);
    }

    @Test
    public void get() {
        assertEquals(INITIAL_VALUE, counter.get());
    }

    @Test
    public void incrementAndGet() {
        final long expected = INITIAL_VALUE + 1;
        assertEquals(expected, counter.incrementAndGet());
        assertEquals(expected, counter.get());
    };

    @Test
    public void getAndIncrement() {
        assertEquals(INITIAL_VALUE, counter.getAndIncrement());
        assertEquals(INITIAL_VALUE + 1, counter.get());
    };

    @Test
    public void decrementAndGet() {
        final long expected = INITIAL_VALUE - 1;
        assertEquals(expected, counter.decrementAndGet());
        assertEquals(expected, counter.get());
    };

    @Test
    public void getAndDecrement() {
        assertEquals(INITIAL_VALUE, counter.getAndDecrement());
        assertEquals(INITIAL_VALUE - 1, counter.get());
    };
}
