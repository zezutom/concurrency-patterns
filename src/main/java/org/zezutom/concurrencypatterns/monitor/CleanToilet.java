package org.zezutom.concurrencypatterns.monitor;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: Tomas Zezula
 * Date: 27/07/2014
 */
public class CleanToilet implements Toilet {

    private final ReentrantLock lock;

    private Condition oneAtATimeCondition;

    private volatile int counter;

    public CleanToilet() {
        lock = new ReentrantLock(true);
        oneAtATimeCondition = lock.newCondition();
    }

    @Override
    public boolean enter() {
        lock.lock();
        try {
            while (counter > 0) {
                oneAtATimeCondition.awaitUninterruptibly();
            }

            if (++counter == 1) {
                oneAtATimeCondition.signal();
            }

            return isOccupied();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void quit() {
        lock.lock();
        try {

            while (counter <= 0) {
                oneAtATimeCondition.awaitUninterruptibly();
            }

            if(isOccupied()) {
                if (--counter == 0) {
                    oneAtATimeCondition.signal();
                }
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean isOccupied() {
        if (counter < 0 || counter > 1) {
            throw new ToiletFloodedException();
        }
        return counter > 0;
    }
}
