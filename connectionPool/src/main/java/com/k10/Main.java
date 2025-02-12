package com.k10;

import com.k10.connectionPool.ConnectionPool;
import com.k10.connectionPool.Database;

import java.sql.Connection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;


public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        //withoutPool();
        withPool();
    }

    private static void withPool() {
        ConnectionPool connectionPool = new ConnectionPool();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        List<Future<String>> futures = IntStream.range(1, 10).mapToObj(
                i -> executorService.submit(() -> {
                    Connection connection = connectionPool.getConnection();
                    String name = Database.executeQuery(connection);
                    connectionPool.putConnection(connection);
                    return name;
                })
        ).toList();
        executorService.shutdown();

        futures.forEach(
                f->{
                    try {
                        System.out.println(f.get());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } catch (ExecutionException e) {
                        System.out.println("*** ERROR **" + e.getMessage());
                    }
                }
        );
    }

    private static void withoutPool() {
        ExecutorService executorService = Executors.newFixedThreadPool(200);

        List<Future<Connection>> futures = IntStream.range(1, 200).mapToObj(
                i -> executorService.submit(Database::newConnection)
        ).toList();

        executorService.shutdown();

        futures.forEach(
                f->{
                    try {
                        f.get();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    } catch (ExecutionException e) {
                        System.out.println("*** ERROR **" + e.getMessage());
                    }
                }
        );
    }
}