package multithreading;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        SharedBuffer sharedBuffer = new SharedBuffer();  // Single shared instance

        // Producer thread
        Thread producer = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                sharedBuffer.produce(i);
            }
        });

        // Consumer thread
        Thread consumer = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                sharedBuffer.consume();
            }
        });

        producer.start();
        Thread.sleep(10000);
        consumer.start();
    }
}
