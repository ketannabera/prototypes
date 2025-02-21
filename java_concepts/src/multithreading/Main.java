package multithreading;

public class Main {
    public static void main(String[] args) {
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
        consumer.start();
    }
}
