package org.zezutom.concurrencypatterns.halfsynchalfasync.test;

import org.junit.Before;
import org.junit.Test;
import org.zezutom.concurrencypatterns.test.util.TestExecutor;

import static org.junit.Assert.assertEquals;
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
}
