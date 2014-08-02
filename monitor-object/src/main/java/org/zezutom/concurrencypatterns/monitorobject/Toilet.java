package org.zezutom.concurrencypatterns.monitorobject;

/**
 * @author Tomas Zezula
 * Date: 27/07/2014
 *
 */
public interface Toilet {

    boolean enter();

    void quit();

    boolean isOccupied();
}
