package org.zezutom.concurrencypatterns.monitorobject;

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
