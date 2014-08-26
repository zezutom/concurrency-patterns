package org.zezutom.concurrencypatterns.test.util;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author: Tomas Zezula
 * Date: 26/08/2014
 */
public class StopWatchTest {

    public static final long SLEEP_MILLIS = 100L;

    private StopWatch stopWatch;

    @Before
    public void init() {
        stopWatch = new StopWatch();
    }

    @Test
    public void startShouldTriggerTheCount() throws InterruptedException {

        stopWatch.start();
        Thread.sleep(SLEEP_MILLIS);

        long t1 = stopWatch.elapsedTime();
        Thread.sleep(SLEEP_MILLIS);

        long t2 = stopWatch.elapsedTime();

        assertTrue(t1 > 0);
        assertTrue(t1 < t2);
    }

    @Test
    public void stopShouldHaltTheCount() throws InterruptedException {

        stopWatch.start();
        Thread.sleep(SLEEP_MILLIS);

        long t1 = stopWatch.elapsedTime();
        stopWatch.stop();

        Thread.sleep(SLEEP_MILLIS);

        long t2 = stopWatch.elapsedTime();

        assertEquals(t1, t2);
    }

}
