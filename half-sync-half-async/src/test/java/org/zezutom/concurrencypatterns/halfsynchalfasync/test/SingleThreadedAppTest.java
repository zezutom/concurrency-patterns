package org.zezutom.concurrencypatterns.halfsynchalfasync.test;

import org.junit.Test;
import org.zezutom.concurrencypatterns.halfsynchalfasync.SingleThreadedApp;

import static org.zezutom.concurrencypatterns.halfsynchalfasync.test.FactorialConstants.*;
import static org.junit.Assert.assertEquals;

/**
 * @author: Tomas Zezula
 * Date: 24/08/2014
 */
public class SingleThreadedAppTest {

    private SingleThreadedApp singleThreadedApp = new SingleThreadedApp();

    @Test
    public void calculationMustBeCorrect() {
        assertEquals(FACTORIAL_1, singleThreadedApp.factorial(0));
        assertEquals(FACTORIAL_1, singleThreadedApp.factorial(1));
        assertEquals(FACTORIAL_5, singleThreadedApp.factorial(5));
        assertEquals(FACTORIAL_11, singleThreadedApp.factorial(11));
    }

}
