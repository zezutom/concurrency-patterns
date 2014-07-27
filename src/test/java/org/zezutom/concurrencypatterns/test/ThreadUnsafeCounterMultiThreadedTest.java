package org.zezutom.concurrencypatterns.test;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zezutom.concurrencypatterns.Counter;
import org.zezutom.concurrencypatterns.ThreadUnsafeCounter;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Tomas Zezula
 *
 * Proves that the implementation of org.zezutom.concurrencypatterns.ThreadUnsafeCounter
 * is NOT thread-safe. The tests "pass" in a a sense that the actual results differ from
 * the expected values.
 */
public class ThreadUnsafeCounterMultiThreadedTest {

    // The value the counter is initialized with
    public static final long INITIAL_VALUE = 10L;

    // Concurrent counter threads: per-method commands
    private static Runnable getCommand;
    private static Runnable incrementAndGetCommand;
    private static Runnable getAndIncrementCommand;
    private static Runnable decrementAndGetCommand;
    private static Runnable getAndDecrementCommand;

    // The number of iteration the commands should be looped over
    public static final int MAX_ITERATIONS = 10000;

    // The number of concurrent threads per each individual command
    public static final int CONCURRENT_THREADS = 5;

    // Ensures all threads are ready when starting a new test
    private static CyclicBarrier startSync;

    // Ensures all threads are done doing their job before a test is terminated
    private static CountDownLatch stopSync;

    // An instance of the tested class. Being 'volatile' indicates it's going to be used by multiple threads
    private static volatile Counter counter;

    // The value of the counter prior to any testing
    private long startValue;

    /**
     * Runs a test by looping a given command until the MAX_ITERATIONS is reached.
     */
    private static class TestRunner implements Runnable {

        private Runnable command;

        private int iterations;

        TestRunner(Runnable command) {
            this.command = command;
        }

        public int getIterations() {
            return iterations;
        }

        @Override
        public void run() {
            try {
                // Wait for all other threads to start running
                startSync.await();

                for (iterations = 0; iterations < MAX_ITERATIONS; iterations++) {
                    command.run();
                }

                // Notify the main thread the job is done
                stopSync.countDown();

            } catch (InterruptedException | BrokenBarrierException e) {
                fail("Command failed to execute.");
            }
        }
    }

    @BeforeClass
    public static void init() {
        // Instantiates the counter with the initial value
        counter = new ThreadUnsafeCounter(INITIAL_VALUE);

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
        runTest(getCommand);
        assertEquals(startValue, counter.get());
    }

    @Test(expected = AssertionError.class)
    public void incrementAndGet() {
        runTest(incrementAndGetCommand);
        assertEquals(getExpectedIncrementedValue(), counter.get());
    }

    @Test(expected = AssertionError.class)
    public void getAndIncrement() {
        runTest(getAndIncrementCommand);
        assertEquals(getExpectedIncrementedValue(), counter.get());
    }

    @Test(expected = AssertionError.class)
    public void decrementAndGet() {
        runTest(decrementAndGetCommand);
        assertEquals(getExpectedDecrementedValue(), counter.get());
    }

    @Test(expected = AssertionError.class)
    public void getAndDecrement() {
        runTest(getAndDecrementCommand);
        assertEquals(getExpectedDecrementedValue(), counter.get());
    }

    @Test(expected = AssertionError.class)
    public void runAll() {
        runTest(null);
        assertEquals(startValue, counter.get());
    }

    private void runTest(Runnable command) {
        try {
            // Tests synchronization
            startSync = new CyclicBarrier(CONCURRENT_THREADS);
            stopSync  = new CountDownLatch(CONCURRENT_THREADS);

            // A number of concurrent tests
            TestRunner[] runners = new TestRunner[CONCURRENT_THREADS];

            if (command == null) {
                // Initialize the runners with the predefined commands
                runners[0] = new TestRunner(getCommand);
                runners[1] = new TestRunner(incrementAndGetCommand);
                runners[2] = new TestRunner(getAndIncrementCommand);
                runners[3] = new TestRunner(decrementAndGetCommand);
                runners[4] = new TestRunner(getAndDecrementCommand);
            }
            else {
                // Initialize the runners with the provided command and start them
                for (int i = 0; i < CONCURRENT_THREADS; i++) {
                    runners[i] = new TestRunner(command);
                }
            }

            // Start the runners
            for (TestRunner runner : runners) {
                new Thread(runner).start();
            }

            // Wait until all of the runners will have finished their job
            stopSync.await();

            // Ensure each and every runner did actually do its job
            for (TestRunner runner : runners) {
                assertEquals("The runner not used to its full potential.", MAX_ITERATIONS, runner.getIterations());
            }

        } catch (InterruptedException e) {
            fail("Exception when running the tests.");
        }
    }

    private long getExpectedIncrementedValue() {
        return startValue + MAX_ITERATIONS * CONCURRENT_THREADS;
    }

    private long getExpectedDecrementedValue() {
        return startValue - MAX_ITERATIONS * CONCURRENT_THREADS;
    }

}
