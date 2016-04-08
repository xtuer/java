import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RedisUtil {
    private Jedis testJedis = null;
    private JedisPool pool = null;
    private boolean connected = false;
    private String redisUrl = "localhost";

    public RedisUtil() {
        connectToRedis();
        testConnection();
    }

    public String get(final String key) {
        return execute(new Executor() {
            @Override
            public String execute(Jedis jedis) {
                return jedis.get(key);
            }
        });
    }

    public void set(final String key, final String value) {
        execute(new Executor() {
            @Override
            public String execute(Jedis jedis) {
                return jedis.set(key, value);
            }
        });
    }

    /**
     * 使用 Executor 的好处是不用每个方法里都处理 Jedis 连接的获取与回收, 连接断开的异常等
     * @param executor
     * @param <T>
     * @return
     */
    public <T> T execute(Executor executor) {
        Jedis jedis = getJedis();

        if (jedis != null) {
            try {
                return executor.execute(jedis);
            } catch (JedisConnectionException ex) {
            } finally {
                jedis.close();
            }
        }

        return null;
    }

    public Jedis getJedis() {
        return connected ? pool.getResource() : null;
    }

    private void testConnection() {
        // 定时测试 Jedis 连接是否有效
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if (connected) {
                    try {
                        testJedis.get("foo_bar_alice");
                    } catch (Exception ex) {
                        System.out.println("Disconnect from Redis");
                        connected = false;
                        testJedis = null;
                    }
                } else {
                    connectToRedis();
                }
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    private void connectToRedis() {
        if (!connected) {
            try {
                pool = new JedisPool(new JedisPoolConfig(), redisUrl);
                testJedis = pool.getResource();
                connected = true;
            } catch(Exception ex) {
                System.out.println("Cannot connect to Redis");
                pool.destroy();
                connected = false;
                testJedis = null;
            }
        }
    }
}
