package org.zezutom.concurrencypatterns.monitor;

/**
 * @author Tomas Zezula
 *
 *
 */
public interface Toilet {

    boolean enter();

    void quit();

    boolean isOccupied();
}
