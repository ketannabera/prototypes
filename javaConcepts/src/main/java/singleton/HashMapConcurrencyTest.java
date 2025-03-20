package singleton;


import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

public class HashMapConcurrencyTest {
    // Custom keys designed to likely hash to same bucket
    static class CollisionKey {
        private final String value;

        public CollisionKey(String value) {
            this.value = value;
        }

        @Override
        public int hashCode() {
            // Force same hash code to simulate collision
            return 1;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            CollisionKey that = (CollisionKey) obj;
            return value.equals(that.value);
        }

        @Override
        public String toString() {
            return value;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        // Number of threads to simulate concurrent access
        int threadCount = 100;

        // Multiple runs to increase chance of observing race condition
        for (int run = 0; run < 10; run++) {
            System.out.println("\nRun " + (run + 1) + ":");
            testConcurrentHashMapInsertion(threadCount);
        }
    }

    static void testConcurrentHashMapInsertion(int threadCount) throws InterruptedException {
        // Non-thread-safe HashMap
        HashMap<CollisionKey, Integer> map = new HashMap<>();

        // Synchronization mechanism to start all threads simultaneously
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(threadCount);

        // Track expected and actual insertions
        int[] insertionAttempts = new int[1];
        int[] successfulInsertions = new int[1];

        // Create and start threads
        for (int i = 0; i < threadCount; i++) {
            final int threadNum = i;
            new Thread(() -> {
                try {
                    // Wait for all threads to be ready
                    startLatch.await();

                    // Unique key for each thread, but designed to hash to same bucket
                    CollisionKey key = new CollisionKey("Key" + threadNum);

                    synchronized (insertionAttempts) {
                        insertionAttempts[0]++;
                    }

                    // Actual insertion
                    map.put(key, threadNum);

                    synchronized (successfulInsertions) {
                        successfulInsertions[0]++;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    endLatch.countDown();
                }
            }).start();
        }

        // Release all threads to start simultaneously
        startLatch.countDown();

        // Wait for all threads to complete
        endLatch.await();

        // Verify results
        System.out.println("Insertion Attempts: " + insertionAttempts[0]);
        System.out.println("Successful Insertions: " + successfulInsertions[0]);
        System.out.println("Map Size: " + map.size());

        // Detect potential data loss
        if (insertionAttempts[0] != map.size()) {
            System.out.println("⚠️ Potential data loss detected!");
        }
    }
}