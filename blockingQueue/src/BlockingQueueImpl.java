import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingQueueImpl {

    private final Integer maxSize;
    private List<Object> items;
    private final ReentrantLock lock;
    private final Condition notFull;
    private final Condition notEmpty;

    public BlockingQueueImpl(Integer maxSize){
        this.maxSize = maxSize;
        this.items = new ArrayList<>();
        this.lock = new ReentrantLock();
        this.notFull = lock.newCondition();
        this.notEmpty = lock.newCondition();
    }

    public void put(Object o) throws InterruptedException {
        lock.lock();
        if (this.isFull()){
            System.out.println("*** PUT WAITING AS LIST FULL ***");
            this.notFull.await(); // Await current thread till getting NOT FULL signal.
        }
        this.items.add(o);
        System.out.println("*** ADDED " + o.toString());
        this.notEmpty.signal();
        lock.unlock();
    }

    public Object get() throws InterruptedException {
        lock.lock();
        if (this.isEmpty()){
            System.out.println("*** GET WAITING AS LIST EMPTY ***");
            this.notEmpty.await(); // Await current thread till getting NOT EMPTY signal.
        }
        Object item = this.items.get(0);
        this.items = this.items.subList(1,this.items.size());
        this.notFull.signal();
        System.out.println("*** RETURNED " + item.toString());
        lock.unlock();
        return item;
    }

    private boolean isFull() {
        return this.items.size() == maxSize;
    }

    private boolean isEmpty() {
        return this.items.isEmpty();
    }
}
