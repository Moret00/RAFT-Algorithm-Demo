package com.mycompany.raftdemo;

import java.util.*;
import java.util.concurrent.*;

/*
DISCLAIMER: This is a simple simulation of the RAFT consensus algorithm designed for educational purposes. 
The delay times and other parameters are tailored specifically for this simulation and may not reflect real-world scenarios. 
To adapt this simulation for practical use, it is essential to adjust these timings and parameters according to your specific context and requirements.
*/

enum State {
    FOLLOWER, CANDIDATE, LEADER         // Possible Server's states.
}

class RAFTServer {
    private State state;    
    private int term;           // Logical time period used to maintain consistency across the distributed system.
    private int votes;          
    private int id;         
    private static final int TIMEOUT = 3000; // Variable used for defining the interval for sending heartbeats by the Leader.

    private static List<RAFTServer> servers = new ArrayList<>();
    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> electionFuture;          // Variable used for holding a reference to a scheduled Task that starts an election.
    private static final int MIN_TIMEOUT = 1500;            // Minimum 1.5 seconds.
    private static final int MAX_TIMEOUT = 3000;            // Maximum 3 seconds.
    private List<LogEntry> log = new ArrayList<>();          // List to store log entries.

    /*
    The `getRandomTimeout` function is responsible for generating a random timeout value within a specified range. 
    This timeout determines how long a server will wait before starting a new election if it hasn't received a heartbeat from a leader.
    */
    private int getRandomTimeout() {
        return MIN_TIMEOUT + new Random().nextInt(MAX_TIMEOUT - MIN_TIMEOUT);
    }

    /*
    RAFTServers inizialization.
    */
    public RAFTServer(int id) {
        this.id = id;
        this.state = State.FOLLOWER;
        this.term = 0;
    }

    /*
    The `start` function initializes the server and schedules an election timeout task to be executed after a random Delay. 
    This delay is determined by the getRandomTimeout() method, which generates a random timeout value between a minimum and maximum duration.
    */
    public void start() {
        System.out.println("Server " + id + " started as FOLLOWER");
        
        // Schedule the election Time-Out.
        electionFuture = executor.schedule(this::startElection, getRandomTimeout(), TimeUnit.MILLISECONDS);
    }

    /*
    The `startElection` function changes the server's state to CANDIDATE, increments the term, and initiates an election by requesting votes from other servers. 
    It then schedules another election timeout to potentially start a new election if needed.
    */
    private synchronized void startElection() {
        if (state == State.LEADER) return;

        state = State.CANDIDATE;
        term++;
        votes = 1;          // Vote for itself.
        System.out.println("Server " + id + " starting election, term: " + term);

        // Broadcast request for votes
        for (RAFTServer server : servers) {
            if (server.id != this.id) {
                server.requestVote(this);
            }
        }

        // Restart election timeout with random Delay.
        electionFuture = executor.schedule(this::startElection, getRandomTimeout(), TimeUnit.MILLISECONDS);
    }

    /*
    The `requestVote` function processes a vote request from another server, updating its term and state if needed. 
    It grants a vote if the requesting server's term matches its own and the server is currently a `FOLLOWER`.
    */
    private synchronized void requestVote(RAFTServer candidate) {
        System.out.println("Server " + id + " received vote request from server " + candidate.id + " for term " + candidate.term);

        if (candidate.term > this.term) {
            this.term = candidate.term;
            this.state = State.FOLLOWER;
            System.out.println("Server " + id + " updated term to " + this.term + " and changed state to FOLLOWER");
        }
        // Decide whether to vote for the candidate.
        if (this.state == State.FOLLOWER && candidate.term == this.term) {
            candidate.receiveVote();
            System.out.println("Server " + id + " voted for server " + candidate.id);
        }
    }
    
    /*
    The `receiveVote` function increments the vote count for the Server and checks if it has received a majority of votes. 
    If so, it transitions to the LEADER state.
    */
    private synchronized void receiveVote() {
        votes++;
        System.out.println("Server " + id + " received vote, total votes: " + votes);
        if (state != State.LEADER && votes > servers.size() / 2) {
            becomeLeader();
        }
    }
    
    /*
    The `becomeLeader` function changes the server's state to `LEADER`, prints a message indicating it has become the leader.
    After that will start sending heartbeats to other Servers.
    */
    private synchronized void becomeLeader() {
        state = State.LEADER;
        System.out.println("Server " + id + " is now the leader for term " + term + "!");
        
        /*
        The Log entries shown are sample entries for demonstration purposes. 
        They illustrate how logs are replicated among servers during leader transitions in the RAFT algorithm simulation.
        */
        
        // Add some Log entries.
        log.add(new LogEntry("Operation 1"));
        log.add(new LogEntry("Operation 2"));
        
        // Start sending heartbeats.
        sendHeartbeats();
        
        // Replicate log entries to followers.
        replicateLogEntries();
    }
    
    /*
    The `sendHeartbeats` function sends heartbeat messages to all follower servers to maintain leadership and prevent them from starting new elections. 
    It then schedules the next heartbeat to be sent after a fixed interval.
    */
    private void sendHeartbeats() {
        if (state != State.LEADER) return; // Only the leader should send heartbeats

        // Send heartbeats to all followers
        System.out.println("Server " + id + " sending heartbeats");
        for (RAFTServer server : servers) {
            if (server.id != this.id) {
                server.receiveHeartbeat(this);
            }
        }

        // Schedule next heartbeat.
        executor.schedule(this::sendHeartbeats, TIMEOUT / 2, TimeUnit.MILLISECONDS);
    }

    /*
    The `receiveHeartbeat` function updates the server's term and state to `FOLLOWER` if the received heartbeat comes from a Server with a higher term.
    This operation stands for indicating a new Leader.
    */
    private synchronized void receiveHeartbeat(RAFTServer leader) {
        if (leader.term > this.term) {
            this.term = leader.term;
            this.state = State.FOLLOWER;
            System.out.println("Server " + id + " received heartbeat from server " + leader.id + " and changed state to FOLLOWER");
        }
    }
    
    /*
    The `replicateLogEntries` function simulates the replication of log entries from the leader to follower servers.
    */
    private synchronized void replicateLogEntries() {
        for (RAFTServer server : servers) {
            if (server.id != this.id) {
                for (int i = 0; i < log.size(); i++) {
                    System.out.println("Log entry replicated to Server " + server.id + ": " + log.get(i));
                }
            }
        }
    }

    public static void addServer(RAFTServer server) {
        servers.add(server);
    }

    public void shutdown() {
        executor.shutdown();
    }
}
