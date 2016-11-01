import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Random;

public class ZSetTest {
    public static final String ACL_KEY = "acl";
    private static Logger logger = LoggerFactory.getLogger(ZSetTest.class);

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("redis.xml");
        RedisTemplate redisTemplate = context.getBean("redisTemplate", RedisTemplate.class);

        redisTemplate.delete(ACL_KEY); // 清除 acl

        // 加入 zset 用 add()
        // 查看是否存在用 rank() != null
        // 一定权值范围内的用 rangeByScore()
        // 个数 size()
        redisTemplate.opsForZSet().add("acl", "Alice", 100);
        redisTemplate.opsForZSet().add("acl", "Bob", 200);
        redisTemplate.opsForZSet().add("acl", "Carry", 300);
        redisTemplate.opsForZSet().add("acl", "Dorrie", 400);

        System.out.println(redisTemplate.opsForZSet().rangeByScore("acl", 100, 200));

        System.out.println(redisTemplate.opsForZSet().rank("acl", "31"));

        System.out.println(redisTemplate.opsForZSet().size("acl"));

        System.out.println(System.currentTimeMillis());
        Random random = new Random();
        for (int i = 0; i < 10; ++i) {
            canAccess(redisTemplate, random.nextInt(2000) + "");
        }
        System.out.println(System.currentTimeMillis());
        System.out.println(redisTemplate.opsForZSet().size("acl"));

        redisTemplate.opsForZSet().remove("acl", "Bob");
        redisTemplate.opsForZSet().remove("acl", "Bob22");
        redisTemplate.opsForZSet().removeRangeByScore("acl", 0, 10000);
    }

    /**
     * 是否允许用户访问
     *
     * @param redisTemplate
     * @param ip
     * @return
     */
    public static boolean canAccess(RedisTemplate redisTemplate, String ip) {
        int maxCount = 2000; // 从配置文件读取

        // [1] 在 IP 列表中则继续
        if (redisTemplate.opsForZSet().rank(ACL_KEY, ip) != null) {
            logger.debug("Allowed, already in: " + ip);
            return true;
        }

        // [2] 不在 IP 列表，但是小于 maxCount，则加入，继续
        if (redisTemplate.opsForZSet().rank(ACL_KEY, ip) == null && redisTemplate.opsForZSet().size(ACL_KEY) < maxCount) {
            logger.debug("Allowed, new added: " + ip);
            redisTemplate.opsForZSet().add(ACL_KEY, ip, System.currentTimeMillis());
            return true;
        }

        // [3] 既不在 IP 列表，同时 IP 列表超过了 maxCount，则不许访问
        logger.debug("Not allowed: " + ip);

        return false;
    }
}
