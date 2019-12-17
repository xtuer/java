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

    @Test
    public void writeActiveMQ() {
        final int count = 2000;
        long start = System.currentTimeMillis();

        int delta = 0;
        for (int i = 0; i < count; i++) {
            messageProducer.sendPerformanceTestMessage();

            delta++;
            if (delta > 500) {
                delta -= 500;
                System.out.printf("写入 ActiveMQ %d 条消息\n", i);
            }
        }

        long elapse = System.currentTimeMillis() - start;
        System.out.printf("写入 ActiveMQ %d 条消息用时 %d 毫秒，每条消息写入需要 %d 毫秒\n", count, elapse, count/elapse);
    }

    @Test
    public void writeMongo() {
        final int count = 20000;
        long start = System.currentTimeMillis();

        int delta = 0;
        for (int i = 0; i < count; i++) {
            Document document = new Document().append("id", idWorker.nextId()).append("createAt", new Date());
            mongoTemplate.insert(document, COLLECTION_TEST);

            delta++;
            if (delta > 500) {
                delta -= 500;
                System.out.printf("写入 MQ %d 条消息\n", i);
            }
        }

        long elapse = System.currentTimeMillis() - start;
        System.out.printf("写入 Mongo %d 条消息用时 %d 毫秒，每条消息写入需要 %d 毫秒\n", count, elapse, count/elapse);
    }
}
