import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Test {
    static ThreadLocal<BigMemory> local = new ThreadLocal<>();

    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(5);

        for (int i = 0; i < 50; ++i) {
            executor.submit(() -> {
                local.set(new BigMemory());
                local.remove();
            });

            Thread.sleep(100);
        }

        System.out.println("End");
    }

    static class BigMemory {
        private long[] m = new long [1024*1024]; // 8M
    }
}
