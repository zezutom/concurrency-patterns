package org.zezutom.concurrencypatterns.halfsynchalfasync;

import java.util.concurrent.*;

/**
 * @author: Tomas Zezula
 * Date: 24/08/2014
 */
public class WorkQueue {

    // Activation List: incoming requests (tasks) are put into a queue
    private volatile BlockingQueue<Callable<Long>> taskQueue = new LinkedBlockingQueue<>();

    public WorkQueue(final MultiThreadedApp application) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(taskQueue.isEmpty())
                    ;   // await tasks

                // This is the actual task scheduler. It only allows for a single task at a time.
                ExecutorService executorService = Executors.newSingleThreadExecutor();

                try {
                    while (true) {
                        if (taskQueue.isEmpty()) {
                            application.onDone();
                            break;
                        }

                        // at some point in the future the calculated value will be available
                        Future<Long> future = executorService.submit(taskQueue.take());
                        while (!future.isDone())
                            ;   // wait until the calculation is complete

                        // publish the result
                        application.onResult(future.get());

                    }
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException("Task execution was failed!");
                }
                finally {
                    executorService.shutdown();
                }
            }
        }).start();

    }

    public void submit(final int n) {
        submit(createTask(n));
    }

    public void submit(final int start, final int end) {
        submit(createTask(start, end));
    }

    private void submit(Callable<Long> task) {
        try {
            taskQueue.put(task);
        } catch (InterruptedException e) {
            throw new RuntimeException("Task scheduling was interrupted!");
        }
    }

    private Callable<Long> createTask(final int n) {
        return new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return new FactorialTask(n).execute();
            }
        };
    }

    private Callable<Long> createTask(final int start, final int end) {
        return new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return new FactorialTask(start, end).execute();
            }
        };
    }
}
