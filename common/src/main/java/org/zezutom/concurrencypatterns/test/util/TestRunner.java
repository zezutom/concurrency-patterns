package org.zezutom.concurrencypatterns.test.util;

import java.util.concurrent.BrokenBarrierException;

import static org.junit.Assert.fail;

/**
 * @author: Tomas Zezula
 * Date: 27/07/2014
 *
 * Executes the provided java.lang.Runnable instances (commands) until the MAX_ITERATIONS is reached.
 */
public class TestRunner implements Runnable {

    private TestExecutor testExecutor;

    private Runnable command;

    private int iterations;

    TestRunner(TestExecutor testExecutor, Runnable command) {
        this.testExecutor = testExecutor;
        this.command = command;
    }

    public int getIterations() {
        return iterations;
    }

    @Override
    public void run() {
        try {
            // Wait for all other threads to start running
            testExecutor.getStartSync().await();

            for (iterations = 0; iterations < testExecutor.getIterations(); iterations++) {
                command.run();
            }

            // Notify the main thread the job is done
            testExecutor.getStopSync().countDown();

        } catch (InterruptedException | BrokenBarrierException e) {
            fail("Command failed to execute.");
        }
    }
}
