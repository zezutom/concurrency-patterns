package org.zezutom.concurrencypatterns.monitorobject;

/**
 * @author Tomas Zezula
 *
 *
 */
public class FilthyToilet implements Toilet {

    private int counter = 0;

    @Override
    public boolean enter() {
        if (!isOccupied()) {
            counter++;
        }
        return isOccupied();
    }

    @Override
    public void quit() {
        counter--;
    }

    @Override
    public boolean isOccupied() {
        if (counter < 0 || counter > 1) {
            throw new ToiletFloodedException();
        }
        return counter > 0;
    }
}
