package org.zezutom.concurrencypatterns.activeobject;

import java.util.concurrent.*;

/**
 * @author Tomas Zezula
 *
 * Implements the org.zezutom.concurrencypatterns.org.zezutom.concurrencypatterns.activeobject.Counter in a way that is suitable
 * for multi-threaded scenarios. The implementation makes use of the Active Object
 * design pattern:
 *
 * - This implementation is referred to as Active Object (active object)
 * - All of the work is done on a private thread
 * - Method calls are enqueued to the active object and the caller is returned to instantly
 *   (method calls on the active object are always non-blocking and asynchronous)
 * - The private thread is essentially a message taskQueue
 * - Messages are being dequeued and executed one at a time
 * - Messages are atomic one to each other, because they are processed sequentially
 * - Private data are accessed from the private thread
 * - Since there is not a 'shared' state, there is no need for additional synchronization either
 */
public class ThreadSafeCounter implements Counter {

    private long value;

    private BlockingQueue<Callable<Long>> taskQueue = new LinkedBlockingQueue<>();

    private BlockingQueue<Long> resultQueue = new LinkedBlockingQueue<>();

    public ThreadSafeCounter(long value) {
        this.value = value;

        new Thread(new Runnable() {
            @Override
            public void run() {
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                try {
                    // busy waiting
                    while (true) {
                        try {
                            Future<Long> future = executorService.submit(taskQueue.take());
                            while (!future.isDone())
                                ; // wait until the results are ready
                            resultQueue.put(future.get());
                        } catch (InterruptedException | ExecutionException  e) {
                            throw new RuntimeException("Task execution was failed!");
                        }
                    }
                }
                finally {
                    executorService.shutdown();
                }
            }
        }).start();
    }

    @Override
    public long get() {
        return enqueueTask(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return value;
            }
        });
    }

    @Override
    public long incrementAndGet() {
        return enqueueTask(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return ++value;
            }
        });
    }

    @Override
    public long getAndIncrement() {
        return enqueueTask(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return value++;
            }
        });
    }

    @Override
    public long decrementAndGet() {
        return enqueueTask(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return --value;
            }
        });
    }

    @Override
    public long getAndDecrement() {
        return enqueueTask(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return value--;
            }
        });
    }

    private long enqueueTask(Callable<Long> task) {
        Long result = null;
        try {
            taskQueue.put(task);

            while (true) {
                result = resultQueue.poll(500, TimeUnit.MILLISECONDS);
                if (result != null) break;
            }
            return result;
        } catch (InterruptedException e) {
            throw new RuntimeException("Task scheduling was interrupted!");
        }
    }
}
