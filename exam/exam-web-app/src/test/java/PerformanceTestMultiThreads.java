import com.exam.mq.MessageProducer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PerformanceTestMultiThreads {
    public static void main(String[] args) throws InterruptedException {
        ApplicationContext context = new ClassPathXmlApplicationContext("config/application.xml");
        MessageProducer messageProducer = context.getBean("mqMessageProducer", MessageProducer.class);

        ExecutorService es = Executors.newFixedThreadPool(5);

        // 同步消息
        // for (int j = 0; j < 1; j++) {
        //     es.submit(() -> {
        //         final int count = 2000;
        //         long start = System.currentTimeMillis();
        //
        //         for (int i = 1; i <= count; i++) {
        //             messageProducer.sendPerformanceTestMessage();
        //
        //             if (i % 200 == 0) {
        //                 System.out.printf("写入 %d 条\n", 200);
        //             }
        //         }
        //
        //         long elapse = System.currentTimeMillis() - start;
        //         System.out.printf("同步消息: 线程 %s 写入 ActiveMQ %d 条消息用时 %d 毫秒，每条消息写入需要 %d 毫秒\n",
        //                 Thread.currentThread().getName(), count, elapse, elapse / count);
        //     });
        // }

        // 异步消息
        for (int j = 0; j < 2; j++) {
            es.submit(() -> {
                final int count = 50000;
                long start = System.currentTimeMillis();

                for (int i = 1; i <= count; i++) {
                    messageProducer.sendPerformanceTestMessageAsync();

                    if (i % 200 == 0) {
                        System.out.printf("写入 %d 条\n", 200);
                    }
                }

                long elapse = System.currentTimeMillis() - start;
                System.out.printf("异步消息: 线程 %s 写入 ActiveMQ %d 条消息用时 %d 毫秒，每条消息写入需要 %d 毫秒\n",
                        Thread.currentThread().getName(), count, elapse, elapse / count);
            });
        }

        es.shutdown();
    }
}
