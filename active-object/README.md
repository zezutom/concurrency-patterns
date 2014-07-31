# Active Object

The goal is to decouple method execution from its invocation. Why? Well, to either achieve
a better throughput via asynchronous method invocation or work around system limitations or both, examples:
- booking system: instantaneous request confirmation vs time of when the order is actually processed
- Android programming - UI changes: a background service sending a message to the UI thread via a message handler

To avoid race conditions, incoming client requests are queued and handled by a scheduler.
The scheduler picks a queued object and makes it run its logic. It is object's responsibility
to know what to do when it gets invoked, hence the Active Object.

## Key Components
- Proxy: provides interface the clients can use to submit their requests
- Activation List: a queue of pending client requests
- Scheduler: decides which request to execute next
- Active Object: implements the core business logic
- Callback: contains execution result (i.e. a promise or a future)

## Pros and Cons
TODO

## Example
source code directories:
- src/main/java/org/zezutom/concurrencypatterns/activeobject
- src/test/java/org/zezutom/concurrencypatterns/activeobject/test

A simple counter implementing a sub-set of the [SimpleAtomicLong](TODO: a link to java.org.concurrent.atomic.SimpleAtomicLong).
The counter keeps its internal state which is then a subject to race conditions:
```java
public class ThreadSafeCounter {
    private long value;
    ..
}
```
The challenge is to ensure the counter consistently yields the correct results, even when many
threads access and modify counter's intrinsic value.

`ThreadUnsafeCounter.java` represents a naive implementation which fails to handle concurrent access.
The failure is proved by a multi-threaded test `ThreadUnsafeCounterMultiThreadedTest.java`:

```java
public class ThreadUnsafeCounterMultiThreadedTest {
    ..
    // Note that a test failure is expected
    @Test(expected = AssertionError.class)
    public void incrementAndGet() {
        testExecutor.runTest(incrementAndGetCommand);
        assertEquals(getExpectedIncrementedValue(), counter.get());
    }
    ..
}
```

`ThreadSafeCounter.java` handles concurrency by using the Active Object design pattern:

```java
public class ThreadSafeCounter {

    // The internal state, subject to race conditions.
    private long value;

    // Activation List: incoming requests (tasks) are put into a queue
    private BlockingQueue<Callable<Long>> taskQueue = new LinkedBlockingQueue<>();

    // Callback: provides access to the calculated results (incrementAndGet, etc.)
    private BlockingQueue<Long> resultQueue = new LinkedBlockingQueue<>();

    // Scheduler: a dedicated thread created and started when the counter gets instantiated
    public ThreadSafeCounter(long value) {
        ..
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    // Constantly watching for incoming requests
                    ..
                }
            }
        }).start();
    }
    ..

    // Proxy: allows the clients to submit new tasks
    private long enqueueTask(Callable<Long> task) {..}
}
```

The implementation offloads the actual task scheduling to the [Executor](TODO: link to java.concurrency.Executor) framework.
The execution results are handled asynchronously via futures. For simplicity, I chose to block
the clients until the results become available. Still in the `ThreadSafeCounter.java`:

```java
    // This is the actual task scheduler. It only allows for a single task at a time.
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    ..
    // At some point in the future the counter's new value will be available
    Future<Long> future = executorService.submit(taskQueue.take());
    ..
    // Meanwhile, the client is blocked until the result is ready
    while (true) {
        Long result = resultQueue.poll(500, TimeUnit.MILLISECONDS);
        if (result != null) break;
    }
    ..
```

## Resources
- [Wikipedia](http://en.wikipedia.org/wiki/Active_object)
- [Prefer Using Active Objects instead of Naked Threads](http://www.drdobbs.com/parallel/prefer-using-active-objects-instead-of-n/225700095)
- [Android Concurrency: The Active Object Pattern](http://www.dre.vanderbilt.edu/~schmidt/cs282/PDFs/6-Concurrency-and-Synchronization-part9.pdf)








