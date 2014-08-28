
| Pattern | Main Features | Drawbacks |                  
| ------- | ------------- | --------- |
| [Active Object](./active-object) | execution in a dedicated thread, allows for complex scheduling, good separation of concerns | performance / code overhead |
| [Monitor Object](./monitor-object) | cooperative execution scheduling, less of performance overhead | tight coupling, unsuitable for advanced scheduling |
| [Half-Sync / Half-Async](./half-sync-half-async) | responsive interface, separation of concerns | performance overhead, harder to debug |
| Leader / Followers | | |
| Thread-Specific Storage | | |

