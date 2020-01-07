import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Test {
    public static void main(String[] args) throws Exception {
        ExecutorService es = Executors.newFixedThreadPool(1, (r) -> {
            Thread t = new Thread(r);
            t.setName("BBB");
            return t;
        });
        es.submit(() -> {
            System.out.println(Thread.currentThread().getName());
        });
        es.shutdown();
    }
}
