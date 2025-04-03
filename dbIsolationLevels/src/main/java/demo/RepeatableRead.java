package demo;

import java.sql.*;
import java.util.concurrent.*;

/**
 * Demonstrates the REPEATABLE READ isolation level in PostgreSQL.
 * Shows how it prevents non-repeatable reads but allows phantom reads.
 */
public class RepeatableRead {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/postgres";

    static void initDatabase() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            
            // Drop tables if they exist
            stmt.execute("DROP TABLE IF EXISTS products");
            
            // Create the products table
            stmt.execute("CREATE TABLE products (" +
                         "id INT PRIMARY KEY, " +
                         "name VARCHAR(50), " +
                         "price DECIMAL(10,2), " +
                         "category VARCHAR(50))");
            
            // Insert initial test data
            stmt.execute("INSERT INTO products VALUES (1, 'Laptop', 999.99, 'Electronics')");
            stmt.execute("INSERT INTO products VALUES (2, 'Smartphone', 699.99, 'Electronics')");
            stmt.execute("INSERT INTO products VALUES (3, 'Headphones', 149.99, 'Electronics')");
            
            System.out.println("Test database initialized with products table");
        }
    }
    
    static void demonstrateRepeatableRead() throws Exception {
        // Create latches for coordination between threads
        CountDownLatch firstReadDone = new CountDownLatch(1);
        CountDownLatch updateDone = new CountDownLatch(1);
        
        // Create an executor for running the two transactions
        ExecutorService executor = Executors.newFixedThreadPool(2);
        
        // Transaction 1: Will perform repeated reads in REPEATABLE READ mode
        executor.submit(() -> {
            try (Connection conn = DriverManager.getConnection(DB_URL)) {
                // Disable auto-commit for transaction control
                conn.setAutoCommit(false);
                
                // Set transaction isolation level to REPEATABLE READ
                conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
                System.out.println("Transaction 1: Set isolation level to REPEATABLE READ");
                
                // First read of product prices
                System.out.println("Transaction 1: First read of Laptop price");
                double firstReadPrice = 0;
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT price FROM products WHERE id = 1")) {
                    if (rs.next()) {
                        firstReadPrice = rs.getDouble("price");
                        System.out.println("Transaction 1: Laptop price is $" + firstReadPrice);
                    }
                }
                
                // Signal first read is complete
                firstReadDone.countDown();
                
                // Wait for Transaction 2 to update the data
                System.out.println("Transaction 1: Waiting for Transaction 2 to update Laptop price...");
                updateDone.await();
                Thread.sleep(1000); // Brief pause
                
                // Second read of the same product in the same transaction
                System.out.println("Transaction 1: Second read of Laptop price");
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT price FROM products WHERE id = 1")) {
                    if (rs.next()) {
                        double secondReadPrice = rs.getDouble("price");
                        System.out.println("Transaction 1: Laptop price is $" + secondReadPrice);
                        
                        // Check for repeatable read
                        if (firstReadPrice == secondReadPrice) {
                            System.out.println("*** REPEATABLE READ CONFIRMED: Both reads returned $" + secondReadPrice + " ***");
                            System.out.println("*** This demonstrates that REPEATABLE READ prevents non-repeatable reads ***");
                        } else {
                            System.out.println("*** UNEXPECTED: Price changed from $" + firstReadPrice + 
                                              " to $" + secondReadPrice + " (should not happen in REPEATABLE READ) ***");
                        }
                    }
                }
                
                // Commit the transaction
                conn.commit();
            } catch (Exception e) {
                System.err.println("Transaction 1 error: " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        });
        
        // Transaction 2: Will update the product price after Transaction 1's first read
        executor.submit(() -> {
            try {
                // Wait for Transaction 1 to perform its first read
                firstReadDone.await();
                Thread.sleep(1000); // Brief pause
                
                // Now update the product price
                try (Connection conn = DriverManager.getConnection(DB_URL)) {
                    conn.setAutoCommit(false);
                    System.out.println("Transaction 2: Updating Laptop price");
                    
                    try (PreparedStatement pstmt = conn.prepareStatement(
                            "UPDATE products SET price = 1299.99 WHERE id = 1")) {
                        int updated = pstmt.executeUpdate();
                        System.out.println("Transaction 2: Updated " + updated + " product(s)");
                    }
                    
                    // Verify the current price from Transaction 2's view
                    try (Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery("SELECT price FROM products WHERE id = 1")) {
                        if (rs.next()) {
                            System.out.println("Transaction 2: Laptop price is now $" + rs.getDouble("price"));
                        }
                    }
                    
                    // Commit the update
                    System.out.println("Transaction 2: Committing price update");
                    conn.commit();
                    
                    // Signal that update is complete
                    updateDone.countDown();
                }
            } catch (Exception e) {
                System.err.println("Transaction 2 error: " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        });

        // Transaction 3: Will read the product price after Transaction 2's commit. So it will still see updated price unlike T1.
        executor.submit(() -> {
            try {
                // Wait for Transaction 2 to update
                updateDone.await();

                // Now update the product price
                try (Connection conn = DriverManager.getConnection(DB_URL)) {
                    System.out.println("Transaction 3: Reading Laptop price");

                    // Verify the current price from Transaction 2's view
                    try (Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery("SELECT price FROM products WHERE id = 1")) {
                        if (rs.next()) {
                            System.out.println("Transaction 3: Laptop price is $" + rs.getDouble("price"));
                        }
                    }

                }
            } catch (Exception e) {
                System.err.println("Transaction 2 error: " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        });
        
        // Shutdown the executor once both tasks are done
        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);
        
        // Print final state
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, name, price FROM products ORDER BY id")) {
            
            System.out.println("\nFinal state of products table:");
            while (rs.next()) {
                System.out.println(rs.getInt("id") + ". " + rs.getString("name") + 
                                   ": $" + rs.getDouble("price"));
            }
        }
        
        System.out.println("\n=== Key Points About REPEATABLE READ in PostgreSQL ===");
        System.out.println("1. Prevents both dirty reads and non-repeatable reads");
        System.out.println("2. Same query will return the same results within a transaction");
        System.out.println("3. Other transactions can still modify data, but those changes won't be visible");
        System.out.println("   to an ongoing transaction that already queried that data");
    }
}