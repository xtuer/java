import java.util.concurrent.*;

public class FutureTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(1);

        // Future 需要和线程池一起使用
        // 得到 Future, 说明其相关的任务已经提交给线程池去执行了
        // Future 用来查看是否已经完成, 取消任务, 获取任务的结果
        Future<Integer> future = executor.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int sum = 0;
                for (int i = 0; i < 100; ++i) {
                    sum += i;
                    Thread.sleep(10);
                }

                return sum;
            }
        });

        executor.shutdown(); // 不再接受新的任务, 已提交的任务会执行完成

        System.out.println("===> main");
        System.out.println(future.get()); // Block
    }
}
