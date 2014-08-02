package org.zezutom.concurrencypatterns.monitorobject.test;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.zezutom.concurrencypatterns.monitorobject.FilthyToilet;
import org.zezutom.concurrencypatterns.monitorobject.Toilet;
import org.zezutom.concurrencypatterns.monitorobject.ToiletFloodedException;
import org.zezutom.concurrencypatterns.test.util.TestExecutor;

import static org.junit.Assert.assertTrue;

/**
 * @author Tomas Zezula
 * Date: 27/07/2014
 */
public class FilthyToiletMultiThreadedTest {

    public static final long MIN_WAIT_MILLIS = 100;

    public static final long MAX_WAIT_MILLIS = 120;

    private static volatile Toilet toilet;

    private static Runnable oneTimePatron;

    private static Runnable peacefulMind;

    private static Runnable frequentFlier;

    private static int toiletFloodedCount;

    private enum Load {
        MODERATE("a moderate"),
        HEAVY("a heavy"),
        EXTREME("an extreme");

        private String value;

        private Load(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    @BeforeClass
    public static void setUp() {

        oneTimePatron = new Runnable() {
            @Override
            public void run() {
                setUsageTest(100, 1);
            }
        };

        peacefulMind = new Runnable() {
            @Override
            public void run() {
                setUsageTest(MAX_WAIT_MILLIS, 1);
            }
        };

        frequentFlier = new Runnable() {
            @Override
            public void run() {
                setUsageTest(105, 3);
            }
        };
    }

    private static void setUsageTest(long acquireMillis, int visitCount) {
        for (int i = 0; i < visitCount; i++) {
            try {
                if (toilet.enter()) {
                    Thread.sleep(acquireMillis);
                    toilet.quit();
                }
            } catch (InterruptedException e) {
                // Don't bother
            } catch (ToiletFloodedException e) {
                toiletFloodedCount++;
            }

            // Another round ahead?
            if (i < visitCount - 1) {
                final long waitPeriod = (long) (Math.random() * (MAX_WAIT_MILLIS - MIN_WAIT_MILLIS) + MIN_WAIT_MILLIS);
                try {
                    Thread.sleep(waitPeriod);
                } catch (InterruptedException e) {
                    // No worries
                }
            }
        }
    }

    @Before
    public void init() {
        toiletFloodedCount = 0;
        toilet = new FilthyToilet();
    }

    @Test
    public void testRegularTraffic() {
        runTest(Load.MODERATE, TestExecutor.get(10, 5), oneTimePatron);
    }

    @Test
    public void testPeakHour() {
        runTest(Load.HEAVY, TestExecutor.get(10, 15), frequentFlier, peacefulMind);
    }

    @Test
    public void testBusyBeyondBelief() {
        runTest(Load.EXTREME, TestExecutor.get(20, 25), oneTimePatron, peacefulMind, frequentFlier);
    }

    private void runTest(Load load, TestExecutor executor, Runnable ... clients) {
        executor.runTest(clients);
        assertIncidents(load);
    }

    private void assertIncidents(Load load) {
        System.out.println(String.format("The toilet was flooded %d times under %s load.", toiletFloodedCount, load));
        assertTrue("The toilet was unexpectedly clean.", toiletFloodedCount > 0);
    }
}
