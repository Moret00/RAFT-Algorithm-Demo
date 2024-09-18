# RAFT-Algorithm 

In distributed systems, data is often replicated across multiple servers to ensure availability and resilience against failures. The Log replicates the operations performed on the system, allowing all nodes to update their data consistently and simultaneously. Without a consensus on which operation to apply and in what order, servers could diverge in their states. If nodes did not agree on a common sequence of operations (Log), they might update data inconsistently, leading to different results on different servers. Consensus on the Log ensures that:

1. All nodes apply the same changes in the same order.
2. Clients see a consistent state of the system, regardless of which server they connect to.

**RAFT** (Reliable, Replicated, Redundant, and Fault-Tolerant) is a distributed consensus protocol used to ensure that a cluster of servers agrees on a sequence of logs despite network failures or crashes. RAFT is designed to be fault-tolerant, meaning it can continue to function correctly even if one or more servers fail (e.g., due to crashes or disconnections). In this context, having an agreed-upon sequence of logs means that even after a failure, when servers recover, they can pick up exactly where they left off, applying the same operations in the same order.

Here’s how it works at a high level:

1. **Initialization and Election Timeout**: Initially, servers do not know who the Leader is. They are all in the Follower state. Each Follower has a random election timeout, which counts the time without receiving communication from a leader. If a Follower does not receive messages (called heartbeats) from the leader within the specified timeout, it assumes there is no active leader. When a Follower exceeds the timeout, it transitions to the Candidate state.

2. **Candidacy**: At this point, the Follower becomes a Candidate. When this happens, it increments its term to start a new election cycle. A term is an integer that is incremented with each election cycle and is used to distinguish between past and current elections, ensuring that each server can vote only once per term. The Candidate sends a Request Vote RPC (Remote Procedure Call) to the other servers in the cluster, asking for their vote. In the request, it includes its current term and its log index, indicating the length of the replicated log of operations it has saved.

3. **Voting**: Each server can vote for only one candidate per term. When a Follower receives a vote request, it compares the term of the Candidate with its own:
   - If the Candidate's term is higher (i.e., more recent), it means the Follower is operating in an old election cycle and updates its own term.
   - If it has not already voted in this term, it can vote for the Candidate.
   - If the Follower's term is higher, it rejects the vote request because it knows it is in a more recent cycle and likely there is already a leader or a more up-to-date candidate.

   Meanwhile, the Candidate continues to gather votes from Followers. If it obtains a majority of the votes (more than 50% of active servers), it becomes the new Leader. Once elected Leader, the Server begins sending regular heartbeats to other servers to maintain leadership.

4. **Conflict Management**: If multiple candidates propose themselves simultaneously, votes may be split such that no candidate reaches a majority. In this case, all candidates fail the election, update their term, and return to the Follower state, waiting for a new election timeout. Since election timeouts are random, one of the servers usually has a shorter timeout and becomes the leader in the next cycle.

5. **Log Replication**: The Leader now manages the cluster and receives all data update requests from clients. Each time the Leader receives a new request, it logs it and sends a message to Followers to replicate the same operation in their logs. When a log entry is replicated to a majority of servers, the Leader considers it committed and confirms the operation to the client.

6. **Failure Management**: If the Leader crashes or stops sending heartbeats, Followers eventually exceed their election timeout and will re-candidate to become the Leader, repeating the process. Even if some servers fail, RAFT operates correctly as long as a majority of servers are active and reach consensus.

In **conclusion**, RAFT ensures that a distributed system remains consistent and resilient despite failures. By using a consensus protocol to agree on a sequence of operations and managing leadership transitions and log replication, RAFT enables a reliable and fault-tolerant system. This design ensures that even in the face of server failures or network issues, the system can continue to operate correctly and provide consistent results across all servers.

A detailed explanation of the code, including class-by-class descriptions, can be found [here](Code-Overview.md).

---
