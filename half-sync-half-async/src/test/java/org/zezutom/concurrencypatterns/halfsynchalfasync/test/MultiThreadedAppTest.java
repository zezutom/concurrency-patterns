package org.zezutom.concurrencypatterns.halfsynchalfasync.test;

import org.junit.Before;
import org.junit.Test;
import org.zezutom.concurrencypatterns.halfsynchalfasync.SingleThreadedApp;
import org.zezutom.concurrencypatterns.test.util.StopWatch;
import org.zezutom.concurrencypatterns.test.util.TestExecutor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.zezutom.concurrencypatterns.halfsynchalfasync.test.FactorialConstants.FACTORIAL_1;
import static org.zezutom.concurrencypatterns.halfsynchalfasync.test.FactorialConstants.FACTORIAL_11;
import static org.zezutom.concurrencypatterns.halfsynchalfasync.test.FactorialConstants.FACTORIAL_5;

/**
 * @author: Tomas Zezula
 * Date: 24/08/2014
 */
public class MultiThreadedAppTest {

    private MultiThreadedAppSubscriber subscriber0;

    private MultiThreadedAppSubscriber subscriber1;

    private MultiThreadedAppSubscriber subscriber5;

    private MultiThreadedAppSubscriber subscriber11;

    private TestExecutor testExecutor;

    @Before
    public void init() {
        testExecutor = TestExecutor.getSingle();
        subscriber0 = new MultiThreadedAppSubscriber(0);
        subscriber1 = new MultiThreadedAppSubscriber(1);
        subscriber5 = new MultiThreadedAppSubscriber(5);
        subscriber11 = new MultiThreadedAppSubscriber(11);
    }

    @Test
    public void calculationMustBeCorrect() {
        testExecutor.runTest(subscriber0, subscriber1, subscriber5, subscriber11);
        assertEquals(FACTORIAL_1, subscriber0.getResult());
        assertEquals(FACTORIAL_1, subscriber1.getResult());
        assertEquals(FACTORIAL_5, subscriber5.getResult());
        assertEquals(FACTORIAL_11, subscriber11.getResult());
    }

    @Test
    public void thereIsOverheadComparedToSingleThread() {

        // Calculate the factorial using a single thread first
        // and capture how long did it take
        StopWatch stopWatch = new StopWatch();
        SingleThreadedApp singleThreadedApp = new SingleThreadedApp();

        stopWatch.start();
        long factorial = singleThreadedApp.factorial(11);
        stopWatch.stop();

        // Now offload the calculation to the multi-threaded implementation
        testExecutor.runTest(subscriber11);

        // Compare the results - sanity check, the returned value must be the same
        assertEquals(factorial, subscriber11.getResult());
        assertTrue(subscriber11.elapsedTime() > stopWatch.elapsedTime());
    }
}
