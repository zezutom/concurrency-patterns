package org.zezutom.concurrencypatterns.halfsynchalfasync;

/**
 * @author: Tomas Zezula
 * Date: 24/08/2014
 */
public interface ResultSubscriber {

    void onResult(boolean result);
}
