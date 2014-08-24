package org.zezutom.concurrencypatterns.halfsynchalfasync;

/**
 * Represents an asynchronous layer, as it forwards client requests for further
 * processing and returns immediately. It receives results via notifications.
 *
 * @author: Tomas Zezula
 * Date: 24/08/2014
 */
public class MultiThreadedApp {

    private long result = 1;

    private WorkQueue queue;

    private ResultSubscriber subscriber;

    public MultiThreadedApp(ResultSubscriber subscriber) {
        queue = new WorkQueue(this);
        this.subscriber = subscriber;
    }

    public void factorial(final int n) {
        Runnable submission = new Runnable() {
            @Override
            public void run() {
                final int processors = Runtime.getRuntime().availableProcessors();
                if (n < processors * 2) {
                    queue.submit(n);
                } else {
                    int batchSize = (n + processors - 1) / processors;

                    for (int i = 1; i <= n; i+= batchSize) {
                        final int start = i;
                        final int end = Math.min(n + 1, i + batchSize);
                        queue.submit(start, end);
                    }
                }
            }
        };
        new Thread(submission).start();
    }

    public void onResult(long result) {
        this.result *= result;
    }

    public void onDone() {
        if (subscriber != null) {
            subscriber.onResult(result);
        }
    }

}
