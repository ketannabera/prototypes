package singleton;

import java.util.HashSet;
import java.util.Set;


public class Main{
    public static void main(String[] args) throws InterruptedException {
        Thread[] threads = new Thread[1000];
        Task[] tasks = new Task[1000];
        for(int i=0;i<1000;i++){
            Task target = new Task();
            threads[i] = new Thread(target);
            tasks[i] = target;
        }
        for(int i=0;i<1000;i++){
            threads[i].start();
        }
        Set<SingletonClass> instances = new HashSet<>();
        for(int i=0;i<1000;i++){
            threads[i].join();
            instances.add(tasks[i].getResult());
        }
    }

}
