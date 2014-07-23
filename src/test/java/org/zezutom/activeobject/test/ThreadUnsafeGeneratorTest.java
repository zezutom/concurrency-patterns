package org.zezutom.activeobject.test;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.zezutom.activeobject.Generator;
import org.zezutom.activeobject.ThreadUnsafeGenerator;
import static org.testng.Assert.*;
/**
 * Created with IntelliJ IDEA.
 * User: tom
 * Date: 23/07/2014
 * Time: 23:06
 * To change this template use File | Settings | File Templates.
 */
public class ThreadUnsafeGeneratorTest {

    private Generator generator;

    @BeforeTest
    public void init() {
        generator = new ThreadUnsafeGenerator();
    }

    @Test
    public void nextIntSingleThread() {
        assertSequence();
    }

    @Test(threadPoolSize = 9, timeOut = 10000, expectedExceptions = {AssertionError.class})
    public void nextIntConcurrent() {
        assertSequence();
    }

    private void assertSequence() {
        assertEquals(generator.nextInt(), 1);
        assertEquals(generator.nextInt(), 2);
        assertEquals(generator.nextInt(), 3);
    }
}
