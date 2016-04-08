import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 使用 spring-data-redis 的 RedisTemplate 访问 Redis, 连接断开后能自动重连
 */
public class RedisTemplateTest {
    static int count = 0;

    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("redis.xml");
        final RedisTemplate redisTemplate = context.getBean("redisTemplate", RedisTemplate.class);

        redisTemplate.opsForValue().set("name", "Biao"); // 设置数据到 Redis
        redisTemplate.opsForValue().set("nginx", "Go");
        System.out.println(redisTemplate.keys("n*")); // 使用模式匹配查找以 n 开头的所有 key

        // 每隔一秒钟从 Redis 里读取一次数据
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println((++count) + ":" + redisTemplate.opsForValue().get("name"));
                } catch(Exception ex) {}
            }
        }, 0, 1, TimeUnit.SECONDS);
    }
}
