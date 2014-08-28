package org.zezutom.concurrencypatterns.halfsynchalfasync.test;

import org.zezutom.concurrencypatterns.halfsynchalfasync.MultiThreadedApp;
import org.zezutom.concurrencypatterns.halfsynchalfasync.ResultSubscriber;

/**
 * @author: Tomas Zezula
 * Date: 24/08/2014
 */
public class MultiThreadedAppSubscriber implements Runnable, ResultSubscriber {

    private boolean result;

    private MultiThreadedApp app;

    private long callTime;

    private long responseTime;

    private String imgPath, outPath;

    public MultiThreadedAppSubscriber(String imgPath, String outPath) {
        this.imgPath = imgPath;
        this.outPath = outPath;
        app = new MultiThreadedApp(this);
    }

    @Override
    public void onResult(boolean result) {
        responseTime = System.currentTimeMillis();
        this.result = result;
    }

    @Override
    public void run() {
        app.convertToAscii(imgPath, outPath);
        callTime = System.currentTimeMillis();
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException("Execution interrupted!");
        }
    }

    public boolean getResult() {
        return result;
    }

    public boolean isAsynchronous() {
        return callTime <= responseTime;
    }
}
