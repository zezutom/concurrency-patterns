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

    // The internal state, subject to race conditions.
    private long value;

    // Activation List: incoming requests (tasks) are put into a queue
    private BlockingQueue<Callable<Long>> taskQueue = new LinkedBlockingQueue<>();

    // Callback: provides access to the calculated results (incrementAndGet, etc.)
    private BlockingQueue<Long> resultQueue = new LinkedBlockingQueue<>();

    // Scheduler: a dedicated thread created and started when the counter gets instantiated
    public ThreadSafeCounter(long value) {
        this.value = value;

        new Thread(new Runnable() {
            @Override
            public void run() {
                // This is the actual task scheduler. It only allows for a single task at a time.
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                try {
                    // busy waiting
                    while (true) {
                        try {
                            // At some point in the future the counter's new value will be available
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
        Long result;
        try {
            // Put the task into the queue
            taskQueue.put(task);

            // Meanwhile, the client is blocked until the result is ready
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
