package com.mycompany.raftdemo;

/*
This is a test main created for experimentation purposes only. 
It is not intended for production use and may not fully represent the final implementation.
*/

public class main {
    
    public static void main(String[] args) throws InterruptedException {
        // Create Servers.
        RAFTServer s1 = new RAFTServer(1);
        RAFTServer s2 = new RAFTServer(2);
        RAFTServer s3 = new RAFTServer(3);

        // Add Servers to the Cluster.
        RAFTServer.addServer(s1);
        RAFTServer.addServer(s2);
        RAFTServer.addServer(s3);

        // Start Servers.
        s1.start();
        s2.start();
        s3.start();

        // Allow some time for the simulation to run.
        Thread.sleep(15000);

        // Shutdown the executor service.
        s1.shutdown();
        s2.shutdown();
        s3.shutdown();
    }
    
}

