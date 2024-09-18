# Code Overview
This simulation provides a basic implementation of the **RAFT algorithm**. It includes a RAFTServer class that represents each server in the cluster, a LogEntry class to model log entries, and a simple main class for running the simulation. <br> Here you can find a brief description of all the classes and their attributes and methods:<br>

**RAFTServer** Class <br><br>
Represents a Server in the RAFT-Cluster. Key components include:

Enum defines the possible states of a Server:

- _FOLLOWER_: A server that is following a leader.
- _CANDIDATE_: A server that is running for leadership.
- _LEADER_: A server that leads the cluster and manages log replication.

Fields: 

- _state_: Current state of the server (FOLLOWER, CANDIDATE, LEADER).
- _term_: Logical time period used to maintain consistency across the distributed system.
- _votes_: Number of votes received by the server during an election.
- _id_: Unique identifier for the server.
- _log_: List of log entries.
- _executor_: ScheduledExecutorService for handling time-based tasks.
- _electionFuture_: Reference to a scheduled election task.

Methods:

- _getRandomTimeout()_: Generates a random timeout value within a specified range for election timeouts.
- _start()_: Initializes the server, sets its state to FOLLOWER, and schedules an election timeout.
- _startElection()_: Transitions the server to CANDIDATE state, initiates an election by requesting votes, and schedules the next election timeout.
- _requestVote(RAFTServer candidate)_: Processes a vote request from another server, updates its term, and grants a vote if eligible.
- _receiveVote()_: Increments the vote count and transitions to LEADER state if a majority of votes is received.
- _becomeLeader()_: Transitions the server to LEADER state, starts sending heartbeats, and replicates log entries to followers.
- _sendHeartbeats()_: Sends heartbeat messages to all follower servers to maintain leadership and prevent new elections.
- _receiveHeartbeat(RAFTServer leader)_: Updates the server's term and state if it receives a heartbeat from a server with a higher term.
- _replicateLogEntries()_: Simulates the replication of log entries from the leader to follower servers.
- _addServer(RAFTServer server)_: Adds a server to the cluster.
- _shutdown()_: Shuts down the executor service. <br><br>

**LogEntry** Class<br><br>
Models a log entry in the RAFT system:

Fields:

- _id_: Unique identifier for the log entry.
- _operation_: Description of the operation represented by the log entry.
- _Constructor_: Initializes a log entry with an operation. 

Methods:

- _toString()_ : Provides a string representation of the log entry for display purposes.<br><br>

**main** Class <br><br>
Contains the main method for running the simulation:

- _Creates Instances_: Initializes three servers (s1, s2, s3).
- _Adds Servers to Cluster_: Adds servers to the RAFT cluster.
- _Starts Servers_: Starts each server.
- _Runs Simulation_: Allows the simulation to run for 15 seconds.
- _Shutdown_: Shuts down the executor services for all servers

All the code in this repository is commented to provide additional information and context. If you need any details or explanations, please refer to the comments within the code.
