# Monitor Object

The pattern revolves around synchronization. In short, concurrent threads (clients) can only use the object via
a set of synchronized methods. Only one method can run at a time. Typically a synchronized method watches for a
certain condition. However, there is no polling involved. Instead, the methods are being notified.
That's a crucial difference in comparison to the [Active Object](../active-object).

Monitor Object is similar to the [Active Object](../active-object) in a sense that it exposes a defined interface via object's synchronized methods.
On the other hand, the methods execute in the client's thread as there is no notion of a centralized thread control. There is no significant performance overhead either, Since inefficient busy-waiting loops (polling) are replaced with notifications.

## Key Components

- __Monitor Object__: exposes synchronized methods as the only means of client access
- __Synchronized Methods__: guarantee thread-safe access to the internal state
- __Monitor Lock__: used by the synchronized methods to serialize method invocation
- __Monitor Condition__: caters for cooperative execution scheduling

## Pros and Cons

Advantages:
- __Simplified synchronization__: All of the hard work is offloaded to the object itself, clients are not concerned with synchronization issues.
- __Cooperative execution scheduling__: Monitor conditions are used to suspend / resume method execution.
- __Reduced performance overhead__: Notifications over inefficient polling.

Drawbacks:
- __Synchronization tightly coupled with core logic__: Synchronization code blends into the business logic which breaks the principle of separation of concerns.
- __Nested monitor lockout problem__: An endless wait for a condition to become true can occur when a monitor object is nested into another kind of its own.
In Java for example, monitor locks are not shared between two separate classes. Thus, it can happen that the outer monitor is never released and any threads
watching that monitor would be kept waiting.

## Example
source code directories:
- `src/main/java/org/zezutom/concurrencypatterns/monitorobject`
- `src/main/java/org/zezutom/concurrencypatterns/monitorobject/test`

Inspired by a blog post [Java Monitor Pattern](http://www.e-zest.net/blog/java-monitor-pattern)
I came up with a hypothetical usage of a public toilet. Not only does it reminiscence of what makes
us all equal, but it also comprises pattern's dominant attributes. A toilet is either occupied or vacant,
hence the locked / unlocked parallel. It also should only be used by a single person at a time (race conditions).

Only a vacant toilet can be entered. Once in, the visitor is granted to leave:
```java
public interface Toilet {

    boolean enter();

    void quit();

    boolean isOccupied();
}
```

Now, the challenge is to ensure, under any circumstances, that the toilet only be used by a single person.
Should that condition fail, the toilet becomes flooded:

```java
public class ToiletFloodedException extends RuntimeException {}
```

An obviously ignorant implementation of the Toilet interface ..:

```java
public class FilthyToilet implements Toilet {

    // Yes, that's the internal state - a concurrent visitor counter.
    private int counter = 0;
    ..
}
```

.. has unavoidable consequences: console output after having run the `FilthyToiletMultiThreadedTest.java`:
```java
The toilet was flooded 25 times under a moderate load.
The toilet was flooded 38 times under a heavy load.
The toilet was flooded 96 times under an extreme load.
```

Please note that the flood-count varies and doesn't always follow the load.

Now, the correct implementation makes use of the Monitor Object pattern:

```java
public class CleanToilet implements Toilet {

    // Monitor Lock used by the synchronized methods
    private final ReentrantLock lock;

    // Monitor Condition - the toilet can only be used by a single person at a time
    private Condition oneAtATimeCondition;

    // The guarded object's state - the 'volatile' flag is crucial for the signalling to work
    private volatile int counter;

    // all of the public methods are synchronized
   ..
}
```
The synchronization is ensured by using a lock along with a condition. The lock holds as long as the condition holds true:

```java
    public boolean enter() {
        lock.lock();
        try {
            while (counter > 0) {   // wait while the toilet is being used
                oneAtATimeCondition.awaitUninterruptibly();
            }

            if (++counter == 1) {
                oneAtATimeCondition.signal();   // the toilet has been successfully acquired
            }

            return isOccupied();
        } finally {
            lock.unlock();
        }
    }
```

## Resources
- [Wikipedia](http://en.wikipedia.org/wiki/Monitor_(synchronization))
- [Monitor Object: An Object Behavioral Pattern for Concurrent Programming](http://www.cs.wustl.edu/~schmidt/PDF/monitor.pdf)
- [Monitor Object Concurrency Pattern](http://www.mijnadres.net/published/Monitor%20Object%20Pattern.pdf)
- [Java Monitor Pattern](http://www.e-zest.net/blog/java-monitor-pattern)
