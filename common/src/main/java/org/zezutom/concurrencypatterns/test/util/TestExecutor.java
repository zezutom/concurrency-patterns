package org.zezutom.concurrencypatterns.test.util;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Tomas Zezula
 * Date: 27/07/2014
 */
public class TestExecutor {

    // The number of iteration the commands should be looped over
    public static final int MAX_ITERATIONS = 10000;

    // The number of concurrent threads per each individual command
    public static final int DEFAULT_CONCURRENT_THREADS = 5;

    private int iterations;

    private int concurrentThreads;

    // Ensures all threads are ready when starting a new test
    private CyclicBarrier startSync;

    // Ensures all threads are done doing their job before a test is terminated
    private CountDownLatch stopSync;

    // A number of concurrent tests
    TestRunner[] runners;

    private TestExecutor(int iterations, int concurrentThreads) {
        this.iterations = iterations;
        this.concurrentThreads = concurrentThreads;
    }

    public static TestExecutor get() {
        return new TestExecutor(MAX_ITERATIONS, DEFAULT_CONCURRENT_THREADS);
    }

    public static TestExecutor getSingle() {
        return get(1, 1);
    }

    public static TestExecutor get(int iterations, int concurrentThreads) {
        return new TestExecutor(iterations, concurrentThreads);
    }

    public void runTest(Runnable... commands) {

        if (commands == null || commands.length == 0) {
            throw new IllegalArgumentException("No commands to execute!");
        }
        try {

            if (commands.length > 1) {
                // Initialize the runners with the predefined commands
                initTestSync(commands.length);
                for (int i = 0; i < runners.length; i++) {
                    runners[i] = new TestRunner(this, commands[i]);
                }
            }
            else {
                // Initialize the runners with the provided command and start them
                initTestSync(concurrentThreads);
                for (int i = 0; i < runners.length; i++) {
                    runners[i] = new TestRunner(this, commands[0]);
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
                assertEquals("The runner not used to its full potential.", iterations, runner.getIterations());
            }

        } catch (InterruptedException | AssertionError e) {
            fail("Exception when running the tests.");
        }
    }

    public CyclicBarrier getStartSync() {
        return startSync;
    }

    public CountDownLatch getStopSync() {
        return stopSync;
    }

    public int getIterations() {
        return iterations;
    }

    private void initTestSync(final int threadCount) {
        // Tests synchronization
        startSync = new CyclicBarrier(threadCount);
        stopSync  = new CountDownLatch(threadCount);
        runners = new TestRunner[threadCount];
    }
}
