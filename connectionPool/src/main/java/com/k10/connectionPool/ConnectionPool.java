package com.k10.connectionPool;

import java.sql.Connection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class ConnectionPool {

    private final BlockingQueue<Connection> queue;

    public ConnectionPool() {
        this.queue = new ArrayBlockingQueue<>(5);
        for(int i=1;i<5;i++){
            this.queue.add(Database.newConnection());
        }
    }

    public Connection getConnection() throws Exception {
        long start = System.currentTimeMillis();
        System.out.println("*** REQUESTING CONNECTION");
        Connection conn = null;
        try {
            conn = this.queue.poll(5, TimeUnit.MILLISECONDS);
            if (conn == null) {
                throw new Exception("No connection available within timeout period");
            }
        } catch (InterruptedException e) {
            System.out.println("** INTERRUPTED");
        }
        System.out.println("*** ACQUIRED CONNECTION in "+ (System.currentTimeMillis() -start));
        return conn;
    }

    public void putConnection(Connection connection){
        System.out.println("*** RETURNING CONNECTION");
        this.queue.add(connection);
        System.out.println("*** CONNECTION ADDED BACK TO POOL");
    }
}
