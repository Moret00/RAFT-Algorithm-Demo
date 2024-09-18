# RAFT-Algorithm-Demo
This project provides a simple simulation of the RAFT consensus algorithm for achieving distributed consensus in distributed systems.
This project provides a simple simulation of the RAFT consensus algorithm, a fundamental protocol for achieving distributed consensus in distributed systems. <br><br>
The simulation demonstrates key RAFT features, including:

- **Leader Election**: Servers initiate elections to select a leader when necessary. The simulation shows how servers transition between follower, candidate, and leader states.
- **Heartbeat Mechanism**: Leaders periodically send heartbeats to followers to maintain leadership and prevent new elections.
- **Log Replication**: The Leader replicates Log entries to all follower servers to ensure consistency. This demo shows how log entries are propagated and acknowledged by followers.

The simulation includes detailed logging to illustrate the election process, heartbeat communication, and log replication. This demo is designed for educational purposes and can be extended for more advanced use cases.<br><br>
Further details on RAFT Algorithm can be found [here]().
