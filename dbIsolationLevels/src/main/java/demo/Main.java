package demo;

public class Main {
    public static void main(String[] args) {
//        readUncommitted();
        repeatableRead();
    }

    private static void readUncommitted() {
        try {
            ReadCommitted.initDatabase();

            System.out.println("=== READ UNCOMMITTED Isolation Level Demonstration ===");

            ReadCommitted.demonstrateDirtyReads();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void repeatableRead() {
        try {
            RepeatableRead.initDatabase();

            System.out.println("=== REPEATABLE READ Isolation Level Demonstration ===");

            RepeatableRead.demonstrateRepeatableRead();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}