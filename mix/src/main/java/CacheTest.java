import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CacheTest {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("caffeine.xml");
        CacheManager cacheManager = context.getBean("cacheManager", CacheManager.class);
        Cache cache = cacheManager.getCache("tickTime");

        String userId  = "1800";
        long interval  = 2000; // 2s
        long deviation = 100; // 偏差为 100ms


        // 使用缓存实现节流
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            // 1. 获取上次缓存时间
            // 2. 如果上次缓存时间不存在，则说明是第一次访问，则缓存
            // 3. 如果上次缓存时间加上时间差小于等于当前时间，则缓存，大于当前时间说明时间差内进行了多次访问，放弃

            long last = Optional.ofNullable(cache.get(userId))
                    .map(c -> c.get().toString())
                    .map(t -> Long.parseLong(t))
                    .orElse(0L);
            long current = Instant.now().toEpochMilli();

            if (last + interval <= current+deviation) {
                cache.put(userId, current);
                System.out.println(LocalDateTime.now());
            }
        }, 0, 200, TimeUnit.MILLISECONDS);
    }
}
