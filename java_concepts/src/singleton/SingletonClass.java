package singleton;

import java.util.concurrent.locks.ReentrantLock;

public class SingletonClass {

    private static SingletonClass INSTANCE;
    private static ReentrantLock lock = new ReentrantLock();

    private static class SingletonClassHolder{
        private static SingletonClass INSTANCE;
        static {
            System.out.println("**** CREATING INSTANCE ***");
            INSTANCE = new SingletonClass();
        }
    }
    private SingletonClass() {
    }

    public static SingletonClass getInstanceOptimized() {
        return SingletonClassHolder.INSTANCE;
    }

    public static SingletonClass getInstance() {
        if (INSTANCE == null) {
            System.out.println("**** FOUND NULL ***");
            lock.lock();
            if (INSTANCE == null) {
                System.out.println("*** CREATING INSTANCE ***");
                INSTANCE = new SingletonClass();
            }
            lock.unlock();
        }
        return INSTANCE;
    }
}
