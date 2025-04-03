package demo;

import java.sql.*;
import java.util.concurrent.*;

/**
 * This example demonstrates the READ COMMITTED isolation level
 * and shows how Postgres doesn't allow dirty reads to occur.
 */
public class ReadCommitted {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/postgres";

    static void initDatabase() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            // Create accounts table
            stmt.execute("DROP TABLE IF EXISTS accounts");
            stmt.execute("CREATE TABLE accounts (id INT PRIMARY KEY, name VARCHAR(50), balance DECIMAL(10,2))");

            // Insert sample data
            stmt.execute("INSERT INTO accounts VALUES (1, 'Alice', 1000.00)");
            stmt.execute("INSERT INTO accounts VALUES (2, 'Bob', 500.00)");

            System.out.println("Database initialized with sample data");
        }
    }

    /**
     * Demonstrates reads in READ COMMITTED
     */
    static void demonstrateDirtyReads() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(1);

        // Thread 1 - Will make 2 updates but won't commit the second one
        Future<?> transaction1 = executor.submit(() -> {
            try (Connection conn = DriverManager.getConnection(DB_URL)) {
                // Begin transaction and disable auto-commit
                conn.setAutoCommit(false);

                System.out.println("\nTransaction 1: Starting transaction with READ COMMITTED");
                conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

                // Check initial balance
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT balance FROM accounts WHERE id = 1")) {
                    if (rs.next()) {
                        System.out.println("Transaction 1: Alice's initial balance: $" + rs.getDouble("balance"));
                    }
                }

                // Update balance
                System.out.println("Transaction 1: Updating Alice's balance from $1000 to $2000");
                try (PreparedStatement pstmt = conn.prepareStatement(
                        "UPDATE accounts SET balance = 2000.00 WHERE id = 1")) {
                    pstmt.executeUpdate();
                    conn.commit();
                }
                System.out.println("Transaction 1: Updating Alice's balance from $2000 to $3000");
                try (PreparedStatement pstmt = conn.prepareStatement(
                        "UPDATE accounts SET balance = 3000.00 WHERE id = 1")) {
                    pstmt.executeUpdate();
                }

                // Signal that the update is done but NOT committed
                latch.countDown();

                // Wait before rolling back to allow Thread 2 to read the committed data
                System.out.println("Transaction 1: Waiting 10 seconds before rolling back...");
                Thread.sleep(10000);

                // Rollback the transaction (changes will be discarded)
                System.out.println("Transaction 1: Rolling back changes");
                conn.rollback();

                // Verify the rollback
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT balance FROM accounts WHERE id = 1")) {
                    if (rs.next()) {
                        System.out.println("Transaction 1: Alice's balance after rollback: $" + rs.getDouble("balance"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Thread 2 - Will only read the committed data
        Future<?> transaction2 = executor.submit(() -> {
            try {
                // Wait for Transaction 1 to update the data (but not commit)
                latch.await();

                try (Connection conn = DriverManager.getConnection(DB_URL)) {
                    // Set isolation level to READ UNCOMMITTED
                    System.out.println("\nTransaction 2: Starting transaction with READ COMMITTED");
                    conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

                    // Read Alice's balance (will see committed changes)
                    System.out.println("Transaction 2: Reading Alice's balance");
                    try (Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery("SELECT balance FROM accounts WHERE id = 1")) {
                        if (rs.next()) {
                            double balance = rs.getDouble("balance");
                            System.out.println("Transaction 2: Alice's balance is $" + balance);

                            if (balance == 2000.00) {
                                System.out.println("*** COMMITTED READ: Transaction 2 can not see uncommitted changes ***");
                            }
                            if(balance == 3000.00){
                                System.out.println("*** UNCOMMITTED READ DETECTED: This data may be rolled back by Transaction 1 ***");
                            }
                        }
                    }
                }

                // Wait for Transaction 1 to rollback
                Thread.sleep(3000);

                // Read Alice's balance again after Transaction 1 has rolled back
                try (Connection conn = DriverManager.getConnection(DB_URL)) {
                    conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);

                    System.out.println("\nTransaction 2: Reading Alice's balance again after Transaction 1 rollback");
                    try (Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery("SELECT balance FROM accounts WHERE id = 1")) {
                        if (rs.next()) {
                            System.out.println("Transaction 2: Alice's balance is now $" + rs.getDouble("balance"));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Wait for both transactions to complete
        transaction1.get();
        transaction2.get();
        executor.shutdown();

        System.out.println("\n=== END OF DEMONSTRATION ===");
    }
}
