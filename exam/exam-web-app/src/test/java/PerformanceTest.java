import com.exam.mq.MessageProducer;
import com.exam.service.IdWorker;
import org.bson.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;

@RunWith(SpringRunner.class)
@ContextConfiguration({"classpath:config/application.xml"})
public class PerformanceTest {
    private static final String COLLECTION_TEST = "performance_test";

    @Autowired
    private MessageProducer messageProducer;

    @Resource(name = "mongoTemplate")
    private MongoTemplate mongoTemplate;

    @Autowired
    private IdWorker idWorker;

    // 同步写入 MQ
    @Test
    public void writeActiveMQ() {
        // 每秒大概 150 条
        final int count = 2000;
        long start = System.currentTimeMillis();

        for (int i = 1; i <= count; i++) {
            messageProducer.sendPerformanceTestMessage();
            threshold(i, 200);
        }

        long elapse = System.currentTimeMillis() - start;
        System.out.printf("写入 ActiveMQ %d 条消息用时 %d 毫秒，每条消息写入需要 %d 毫秒\n", count, elapse, elapse / count);
    }

    // 异步写入 MQ
    @Test
    public void writeActiveMQAsync() {
        // 2 万条写入大概 1.4S
        final int count = 20000;
        long start = System.currentTimeMillis();

        for (int i = 1; i <= count; i++) {
            messageProducer.sendPerformanceTestMessageAsync();
            threshold(i, 2000);
        }

        long elapse = System.currentTimeMillis() - start;
        System.out.printf("写入 ActiveMQ %d 条消息用时 %d 毫秒，每条消息写入需要 %d 毫秒\n", count, elapse, elapse / count);
    }

    // 写入 Mongo
    @Test
    public void writeMongo() {
        // 2 万条写入大概 1.1S
        final int count = 20000;
        long start = System.currentTimeMillis();

        for (int i = 1; i <= count; i++) {
            Document document = new Document().append("id", idWorker.nextId()).append("createAt", new Date());
            mongoTemplate.insert(document, COLLECTION_TEST);

            threshold(i, 500);
        }

        long elapse = System.currentTimeMillis() - start;
        System.out.printf("写入 Mongo %d 条消息用时 %d 毫秒，每条消息写入需要 %d 毫秒\n", count, elapse, elapse/count);
    }

    public static void threshold(int sn, int size) {
        if (sn % size == 0) {
            System.out.printf("写入 %d 条\n", sn);
        }
    }
}
