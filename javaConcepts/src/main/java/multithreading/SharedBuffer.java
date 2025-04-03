package multithreading;

import java.util.LinkedList;
import java.util.Queue;

class SharedBuffer {
    private Queue<Integer> buffer = new LinkedList<>();
    private int capacity = 5;

    public synchronized void produce(int item) {
        while (buffer.size() == capacity) {
            try {
                wait();  // wait on 'this' SharedBuffer instance
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        buffer.add(item);
        System.out.println("Produced: " + item);
        notifyAll();
//        try {
//            notifyAll();
//            //wait();
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
    }

    public synchronized int consume() {
        while (buffer.isEmpty()) {
            try {
                wait();  // wait on same SharedBuffer instance
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        int item = buffer.remove();
        System.out.println("Consumed: " + item);
        try {
            notifyAll();
            wait();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return item;
    }
}

// Usage:

