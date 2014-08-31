# Half-Sync / Half-Async

The pattern separates asynchronous I/O from the synchronous one. Main thread doesn't block on incoming client requests and long-running operations are offloaded
to a dedicated synchronous layer. Processing results are delivered by the means of callbacks.

Examples:
- largely used in operating systems (hardware interrupts, application management)
- Android programming - [AsyncTask](http://developer.android.com/reference/android/os/AsyncTask.html) (file downloads ..)

A decent queuing system is required to handle messaging between the two layers. The challenge
lies in preventing race conditions and other concurrency related issues.

## Key Components
- __Synchronous Service Layer__: deals with long-running tasks, implements the core business logic
- __Queuing Layer__: a request queue, doesn't block the caller
- __Asynchronous Service Layer__: dispatches incoming requests to the queue

There might be a number of concurrently running synchronous services. The queueing layer
is responsible for thread synchronization.

## Pros and Cons
Advantages:
- __Reduced code complexity__: Synchronized services focus solely on the core logic.
- __Separation of concerns__: Each of the layers is relatively self-contained and serves a different purpose.
- __Centralized communication__: The queuing layer mediates all communication - no moving parts flying around


Drawbacks:
- __Performance overhead__: "Boundary-crossing penalty" - context switching, synchronization, data transfer ..
- __Harder to debug__: Asynchronous callbacks make testing and debugging less straightforward
- __Benefit questionable__: Higher-level application services may not benefit from asynchronous I/O. That depends on
framework / OS design.

## Example
source code directories:
- `src/main/java/org/zezutom/concurrencypatterns/halfsynchalfasync`
- `src/main/java/org/zezutom/concurrencypatterns/halfsynchalfasync/test`

An ASCII Art generator (credit goes to [Evilzone](https://evilzone.org/java/(java-code)image-to-ascii-art-generator))
is not only pleasant to work with, but it is also a suitable candidate for a long-running task. I saw it as a perfect
fit for the pattern.

```java
public class AsciiArtGenerator {
    ..
    /**
     * Converts an image to its ASCII representation.
     *
     * @param imgPath   path to the image, relative to /src/main/resources
     * @param outPath   path to the resulting text file, relative to ./data
     * @return true, if the conversion succeeds, false otherwise
     */
    public boolean convertToAscii(String imgPath, String outPath) {..}
}
```

The image-to-text conversion is a synchronous blocking task, which might take a while
to complete. As such it's bound to run in a background thread.

The front-end of the app is served asynchronously via a non-blocking dispatcher:

```java
/**
 * Represents an asynchronous layer, as it forwards client requests for further
 * processing and returns immediately. It receives results via notifications.
 *
 * @author: Tomas Zezula
 * Date: 24/08/2014
 */
public class NonBlockingDispatcher {
    ..
    /**
     * Sends a request to the queue and returns instantly.
     *
     * @param imgPath   Image path for the ASCII generator
     * @param outPath   Output path for the ASCII generator
     */
    public void dispatch(final String imgPath, final String outPath) {..}

    /**
     * Captures processing result and notifies the subscribed client
     *
     * @param result true, if success, false otherwise
     */
    public void onResult(boolean result) {..}
}
```

Finally, the communication between the dispatcher and the worker thread is mediated by a dedicated queuing channel:

```java
/**
 * Queues incoming requests and notifies the dispatcher when the response is ready.
 *
 * @author: Tomas Zezula
 * Date: 24/08/2014
 */
public class WorkQueue {..}
```

As usual, the example is accompanied by unit tests proving the core concepts. This time,
I kept the tests to a bare minimum, just to highlight the major difference between a naive
single-threaded synchronous approach and the slightly more advanced asynchronous implementation.

The app pays tribute to a great actress of the last century. The resulting file (./data/audrey.txt)
is best viewed using a minimal font-size.

## Resources
- [The Half-Sync/Half-Async Pattern](http://www.dre.vanderbilt.edu/~schmidt/cs282/PDFs/6-Concurrency-and-Synchronization-part10.pdf)
- [Half-Sync/Half-Async](http://www.cs.wustl.edu/~schmidt/PDF/PLoP-95.pdf)
- [Coursera video lecture: Half-Sync/Half-Async Pattern](https://class.coursera.org/posa-002/lecture/211)
- [Android - AsyncTask](http://developer.android.com/reference/android/os/AsyncTask.html)
- [ASCII Art Generator](https://evilzone.org/java/(java-code)image-to-ascii-art-generator/)
- [Portrait of Audrey Hepburn](http://www.topbesthdpicture.com/wp-content/uploads/2014/05/1152630696_1024x768_audrey-hepburn-230x130.jpg)








