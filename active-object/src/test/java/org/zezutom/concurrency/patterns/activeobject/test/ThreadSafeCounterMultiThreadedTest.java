package org.zezutom.concurrency.patterns.activeobject.test;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zezutom.concurrencypatterns.activeobject.Counter;
import org.zezutom.concurrencypatterns.activeobject.ThreadSafeCounter;
import org.zezutom.concurrencypatterns.test.util.TestExecutor;

import static org.junit.Assert.assertEquals;

/**
 * @author Tomas Zezula
 *
 * Proves that the implementation of org.zezutom.concurrencypatterns.org.zezutom.concurrencypatterns.activeobject.ThreadSafeCounter
 * is thread-safe, as the counter - under race conditions - consistently returns expected values.
 */
public class ThreadSafeCounterMultiThreadedTest {

    // The value the counter is initialized with
    public static final long INITIAL_VALUE = 10L;

    // Concurrent counter threads: per-method commands
    private static Runnable getCommand;
    private static Runnable incrementAndGetCommand;
    private static Runnable getAndIncrementCommand;
    private static Runnable decrementAndGetCommand;
    private static Runnable getAndDecrementCommand;

    // Multi-threaded org.zezutom.concurrencypatterns.monitorobject.test executor
    private static TestExecutor testExecutor;

    // An instance of the tested class. Being 'volatile' indicates it's going to be used by multiple threads
    private static volatile Counter counter;

    // The value of the counter prior to any testing
    private long startValue;

    @BeforeClass
    public static void init() {
        // Instantiates the counter with the initial value
        counter = new ThreadSafeCounter(INITIAL_VALUE);

        // Initializes multi-threaded org.zezutom.concurrencypatterns.monitorobject.test executor
        testExecutor = TestExecutor.get();

        // Initializes individual commands
        getCommand =                new Runnable() {@Override public void run() { counter.get(); } };
        incrementAndGetCommand =    new Runnable() {@Override public void run() { counter.incrementAndGet(); } };
        getAndIncrementCommand =    new Runnable() {@Override public void run() { counter.getAndIncrement(); } };
        decrementAndGetCommand =    new Runnable() {@Override public void run() { counter.decrementAndGet(); } };
        getAndDecrementCommand =    new Runnable() {@Override public void run() { counter.getAndDecrement(); } };
    }

    @Before
    public void setUp() {
        startValue = counter.get();
    }

    @Test
    public void get() {
        testExecutor.runTest(getCommand);
        assertEquals(startValue, counter.get());
    }

    @Test
    public void incrementAndGet() {
        testExecutor.runTest(incrementAndGetCommand);
        assertEquals(getExpectedIncrementedValue(), counter.get());
    }

    @Test
    public void getAndIncrement() {
        testExecutor.runTest(getAndIncrementCommand);
        assertEquals(getExpectedIncrementedValue(), counter.get());
    }

    @Test
    public void decrementAndGet() {
        testExecutor.runTest(decrementAndGetCommand);
        assertEquals(getExpectedDecrementedValue(), counter.get());
    }

    @Test
    public void getAndDecrement() {
        testExecutor.runTest(getAndDecrementCommand);
        assertEquals(getExpectedDecrementedValue(), counter.get());
    }

    @Test
    public void runAll() {
        testExecutor.runTest(getCommand,
                incrementAndGetCommand,
                getAndIncrementCommand,
                decrementAndGetCommand,
                getAndDecrementCommand);
        assertEquals(startValue, counter.get());
    }

    private long getExpectedIncrementedValue() {
        return startValue + TestExecutor.MAX_ITERATIONS * TestExecutor.DEFAULT_CONCURRENT_THREADS;
    }

    private long getExpectedDecrementedValue() {
        return startValue - TestExecutor.MAX_ITERATIONS * TestExecutor.DEFAULT_CONCURRENT_THREADS;
    }

}
