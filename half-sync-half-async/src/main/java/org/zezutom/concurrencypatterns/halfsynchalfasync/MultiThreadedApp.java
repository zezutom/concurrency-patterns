package org.zezutom.concurrencypatterns.halfsynchalfasync;

/**
 * Represents an asynchronous layer, as it forwards client requests for further
 * processing and returns immediately. It receives results via notifications.
 *
 * @author: Tomas Zezula
 * Date: 24/08/2014
 */
public class MultiThreadedApp {

    private boolean result = false;

    private WorkQueue queue;

    private ResultSubscriber subscriber;

    public MultiThreadedApp(ResultSubscriber subscriber) {
        queue = new WorkQueue(this);
        this.subscriber = subscriber;
    }

    public void convertToAscii(final String imgPath, final String outPath) {
        Runnable submission = new Runnable() {
            @Override
            public void run() {
                queue.submit(imgPath, outPath);
            }
        };
        new Thread(submission).start();
    }

    public void onResult(boolean result) {
        this.result = result;
    }

    public void onDone() {
        if (subscriber != null) {
            subscriber.onResult(result);
        }
    }

}
