import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Test {
    static ThreadLocal<BigMemory> local = new ThreadLocal<>();

    public static void main(String[] args) throws Exception {
        ExecutorService es = Executors.newFixedThreadPool(5);

        for (int i = 0; i < 50; ++i) {
            es.submit(() -> {
                local.set(new BigMemory());
                local.remove();
            });

            Thread.sleep(100);
        }

        es.shutdown();
        es.awaitTermination(5, TimeUnit.SECONDS);
        System.out.println("End");
    }

    static class BigMemory {
        private long[] m = new long [1024*1024]; // 8M
    }
}
