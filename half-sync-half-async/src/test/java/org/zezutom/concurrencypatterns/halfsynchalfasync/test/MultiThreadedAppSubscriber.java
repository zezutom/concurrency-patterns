package org.zezutom.concurrencypatterns.halfsynchalfasync.test;

import org.zezutom.concurrencypatterns.halfsynchalfasync.MultiThreadedApp;
import org.zezutom.concurrencypatterns.halfsynchalfasync.ResultSubscriber;
import org.zezutom.concurrencypatterns.test.util.StopWatch;

/**
 * @author: Tomas Zezula
 * Date: 24/08/2014
 */
public class MultiThreadedAppSubscriber implements Runnable, ResultSubscriber {

    private int n;

    private long result;

    private StopWatch stopWatch;

    private MultiThreadedApp app;

    public MultiThreadedAppSubscriber(int n) {
        this.n = n;
        stopWatch = new StopWatch();
        app = new MultiThreadedApp(this);
    }

    @Override
    public void onResult(long result) {
        stopWatch.stop();
        this.result = result;
    }

    @Override
    public void run() {
        stopWatch.start();
        app.factorial(n);
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException("Execution interrupted!");
        }
    }

    public long getResult() {
        return result;
    }

    public long elapsedTime() {
        return stopWatch.elapsedTime();
    }
}
