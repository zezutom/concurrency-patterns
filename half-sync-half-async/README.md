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

## Resources
- [The Half-Sync/Half-Async Pattern (opens as PDF)](http://www.dre.vanderbilt.edu/~schmidt/cs282/PDFs/6-Concurrency-and-Synchronization-part10.pdf)
- [Half-Sync/Half-Async (opens as PDF)](http://www.cs.wustl.edu/~schmidt/PDF/PLoP-95.pdf)
- [Coursera video lecture: Half-Sync/Half-Async](https://class.coursera.org/posa-002/lecture/211)
- [Android - AsyncTask](http://developer.android.com/reference/android/os/AsyncTask.html)








