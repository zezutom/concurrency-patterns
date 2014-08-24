package org.zezutom.concurrencypatterns.halfsynchalfasync;

/**
 * A usual single-threaded implementation.
 *
 * @author: Tomas Zezula
 * Date: 24/08/2014
 */
public class SingleThreadedApp {

    public long factorial(int n) {
        return new FactorialTask(n).execute();
    }

    public static void main(String[] args) {
        long factorial = new SingleThreadedApp().factorial(11);
        System.out.println("RESULT: " + factorial);
    }
}
