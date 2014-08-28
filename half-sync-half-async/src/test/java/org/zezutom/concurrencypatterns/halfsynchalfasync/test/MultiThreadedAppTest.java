package org.zezutom.concurrencypatterns.halfsynchalfasync.test;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.zezutom.concurrencypatterns.test.util.DataUtil;
import org.zezutom.concurrencypatterns.test.util.TestExecutor;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertTrue;
/**
 * @author: Tomas Zezula
 * Date: 24/08/2014
 */
public class MultiThreadedAppTest {

    public static final String IMAGE = "audrey_hepburn.jpeg";

    public static final String OUT_TEST = "audrey-test.txt";

    public static final String OUT_ORIGINAL = "audrey.txt";

    private MultiThreadedAppSubscriber subscriber;

    @Before
    public void init() {
        subscriber = new MultiThreadedAppSubscriber(IMAGE, OUT_TEST);
    }

    @After
    public void cleanUp() {
        final File asciiFile = DataUtil.getFile(OUT_TEST);
        if (asciiFile.exists()) asciiFile.delete();
    }

    @Test
    public void asyncAsciiArtRocks() throws IOException {
        TestExecutor.getSingle().runTest(subscriber);
        assertTrue(subscriber.getResult());
        assertTrue(FileUtils.contentEquals(DataUtil.getFile(OUT_ORIGINAL), DataUtil.getFile(OUT_TEST)));
        assertTrue(subscriber.isAsynchronous());
    }
}
