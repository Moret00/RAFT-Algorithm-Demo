package com.mycompany.raftdemo;

/*
The structure of these logs is designed to facilitate the simulation and illustrate how log replication works during leader transitions in the RAFT algorithm.
*/

class LogEntry {
    private static int count = 0;
    private int id;
    private String operation;

    public LogEntry(String operation) {
        this.id = count++;
        this.operation = operation;
    }

    @Override
    public String toString() {
        return "LogEntry {id=" + id + ", operation='" + operation + "'} ";
    }
}