package org.zezutom.concurrencypatterns.halfsynchalfasync.test;

import org.zezutom.concurrencypatterns.halfsynchalfasync.MultiThreadedApp;
import org.zezutom.concurrencypatterns.halfsynchalfasync.ResultSubscriber;

/**
 * @author: Tomas Zezula
 * Date: 24/08/2014
 */
public class MultiThreadedAppSubscriber implements Runnable, ResultSubscriber {

    private int n;

    private long result;

    private MultiThreadedApp app;

    public MultiThreadedAppSubscriber(int n) {
        this.n = n;
        app = new MultiThreadedApp(this);
    }

    @Override
    public void onResult(long result) {
        this.result = result;
    }

    @Override
    public void run() {
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
}
