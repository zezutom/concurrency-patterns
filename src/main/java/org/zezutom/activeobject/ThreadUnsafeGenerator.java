package org.zezutom.activeobject;

/**
 * Created with IntelliJ IDEA.
 * User: tom
 * Date: 23/07/2014
 * Time: 23:05
 * To change this template use File | Settings | File Templates.
 */
public class ThreadUnsafeGenerator implements Generator {

    private int counter;
    @Override
    public int nextInt() {
        return ++counter;
    }
}
