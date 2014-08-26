package org.zezutom.concurrencypatterns.test.util;

/**
 * @author: Tomas Zezula
 * Date: 26/08/2014
 */
public class StopWatch {

    private long startTime = 0;

    private long stopTime = 0;

    private boolean running = false;

    public void start() {
        startTime = System.currentTimeMillis();
        running = true;
    }

    public void stop() {
        stopTime = System.currentTimeMillis();
        running = false;
    }

    public long elapsedTime() {
        return (running) ? System.currentTimeMillis() - startTime : stopTime - startTime;
    }
}
