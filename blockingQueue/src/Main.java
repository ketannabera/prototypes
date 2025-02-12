import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(50);
        BlockingQueueImpl queue = new BlockingQueueImpl(10);

        IntStream.range(1, 25).forEach(
                i -> {
                    executorService.submit(() -> {
                        try {
                            queue.put(i);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
        );

        IntStream.range(1, 25).forEach(i -> executorService.submit(queue::get));

        executorService.shutdown();
    }
}