import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 使用原始的 Redis Client 访问 Redis, 并实现自动重连
 */
public class RedisClientTest {
    private static RedisUtil redisUtil = new RedisUtil();
    private static int count = 0;

    public static void main(String[] args) {
        redisUtil.set("foo", System.nanoTime() + "");

        // 每隔一秒钟从 Redis 里读取一次数据
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                System.out.println((++count) + " : " + redisUtil.get("foo"));
            }
        }, 0, 1, TimeUnit.SECONDS);
    }
}
