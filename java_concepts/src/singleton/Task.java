package singleton;

import lombok.Getter;

public class Task implements Runnable{
    @Getter
    SingletonClass result;

    @Override
    public void run() {
        result = SingletonClass.getInstanceOptimized();
    }
}
