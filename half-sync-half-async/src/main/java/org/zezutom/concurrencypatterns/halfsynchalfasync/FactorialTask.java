package org.zezutom.concurrencypatterns.halfsynchalfasync;

/**
 * Synchronous layer - a potentially long-running calculation.
 *
 * @author: Tomas Zezula
 * Date: 24/08/2014
 */
public class FactorialTask {

    private int start, end;

    public FactorialTask(int n) {
        this(1, n + 1);
    }
    public FactorialTask(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public long execute() {
        long result = start;

        for (int i = start + 1; i < end; i++) {
            result *= i;
        }
        return result;
    }

}
