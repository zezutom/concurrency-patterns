package org.zezutom.concurrencypatterns.monitorobject;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Tomas Zezula
 * Date: 27/07/2014
 */
public class CleanToilet implements Toilet {

    // Monitor Lock used by the synchronized methods
    private final ReentrantLock lock;

    // Monitor Condition - the toilet can only be used by a single person at a time
    private Condition oneAtATimeCondition;

    // The guarded object's state - the 'volatile' flag is crucial for the signaling to work
    private volatile int counter;

    public CleanToilet() {
        lock = new ReentrantLock(true);
        oneAtATimeCondition = lock.newCondition();
    }

    @Override
    public boolean enter() {
        lock.lock();
        try {
            while (counter > 0) {   // wait while the toilet is being used
                oneAtATimeCondition.awaitUninterruptibly();
            }

            if (++counter == 1) {
                oneAtATimeCondition.signal();   // the toilet has been successfully acquired
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

            while (counter <= 0) {  // leaving a vacant toilet makes no sense
                oneAtATimeCondition.awaitUninterruptibly();
            }

            if(isOccupied()) {
                if (--counter == 0) {
                    oneAtATimeCondition.signal();   // the toilet is free to use from this point on
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
