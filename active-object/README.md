# Active Object

The goal is to decouple method execution from its invocation. Why? Well, to either achieve
a better throughput via asynchronous method invocation or work around system limitations or both, examples:
- booking system: instantaneous request confirmation vs time of when the order is actually processed
- Android programming - UI changes: a background service sending a message to the UI thread via a message handler

To avoid race conditions, incoming client requests are queued and handled by a scheduler.
The scheduler picks a queued object and makes it run its logic. It is object's responsibility
to know what to do when it gets invoked, hence the Active Object.

## Key Components
- __Proxy__: provides interface the clients can use to submit their requests
- __Activation List__: a queue of pending client requests
- __Scheduler__: decides which request to execute next
- __Active Object__: implements the core business logic
- __Callback__: contains execution result (i.e. a promise or a future)

## Pros and Cons
On the bright side:
- __Reduced code complexity__: Once pattern's mechanics are in place, the code can be treated as single-threaded.
- __No need for additional synchronization__: Concurrent requests are serialized and handled by a single internal thread

On the down side:
- __Performance overhead__: Sophisticated scheduling, spinning and request handling can be expensive in terms of memory and can lead to non-trivial context switching.
- __Programming overhead__: Active Object essentially requires you to create a small framework. It can definitely be kept self-contained enough, but it boils down to a simple the fact that you need to be aware of multiple components:

Activation List - the queue of incoming requests

Callback - yields the results

Scheduler thread - watches for incoming requests

Scheduler implementation - enqueues requests

Proxy - client interface allowing to submit requests

Future - an asynchronous response

## Example
source code directories:
- `src/main/java/org/zezutom/concurrencypatterns/activeobject`
- `src/main/java/org/zezutom/concurrencypatterns/activeobject/test`

A simple counter implementing a sub-set of the [AtomicLong](http://docs.oracle.com/javase/7/docs/api/java/util/concurrent/atomic/AtomicLong.html).
The counter keeps its internal state which is then a subject to race conditions:
```java
public class ThreadSafeCounter implements Counter {
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
public class ThreadSafeCounter implements Counter {

    // The internal state, subject to race conditions.
    private long value;

    // Activation List: incoming requests (tasks) are put into a queue
    private BlockingQueue<Callable<Long>> taskQueue = new LinkedBlockingQueue<>();

    // Callback: provides access to the calculated results (incrementAndGet, etc.)
    private BlockingQueue<Long> resultQueue = new LinkedBlockingQueue<>();

    // Scheduler: a dedicated thread created and started when the counter is instantiated
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

The implementation offloads the actual task scheduling to the [Executor](http://docs.oracle.com/javase/tutorial/essential/concurrency/exinter.html) framework.
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
- [The Pragmatic Bookshelf: Java Active Objects](http://pragprog.com/magazines/2013-05/java-active-objects)
- [Android Concurrency: The Active Object Pattern](http://www.dre.vanderbilt.edu/~schmidt/cs282/PDFs/6-Concurrency-and-Synchronization-part9.pdf)









