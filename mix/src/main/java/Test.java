import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Test {
    public static void main(String[] args) throws Exception {
        Thread t1 = new Thread(() -> {
            System.out.println("In thread 1");
            throw new RuntimeException("Exception in thread 1");
        });
        t1.setUncaughtExceptionHandler((Thread t, Throwable e) -> {
            System.out.println(t);
            System.out.println(e.getMessage());
        });
        t1.start();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {});
    }
}
