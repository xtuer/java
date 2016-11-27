import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EhcacheWithListener {
    /**
     * 获取配置中的缓存
     * @param cacheManager
     * @param cacheName
     */
    public static void useConfiguredCache(CacheManager cacheManager, String cacheName) {
        Cache cache = (Cache) cacheManager.getCache(cacheName);

        cache.put(new Element("password", "S--S"));
        cache.put(new Element("username", "Viva")); // notifyElementPut()
        cache.put(new Element("username", "Pandora")); // notifyElementUpdated()
        System.out.printf("Size of cache %s: %d\n", cacheName, cache.getSize());

        cache.remove("username"); // 触发 notifyElementRemoved()
        cache.put(new Element("username", "Viva"));
    }

    /**
     * 即使元素过期了, 但是 Cache 不会马上就判断出它已经过期, 只有对其执行 get() 访问或者
     * Cache 中的元素数量大于 maxElementsInMemory 时才会检查元素是否过期.
     * 为了及时的发现元素过期了, 启动一个线程定期调用 evictExpiredElements() 检查出过期的元素,
     * 然后及时, 自动的把它们从缓存里删除.
     *
     * 某些场景下手动检测元素过期是很有必要的, 例如心跳检测数据是放到缓存里的, 当一定时间内没有接收到心跳数据时,
     * 即缓存里的心跳数据就会过期, 不过 EhCache 又不会及时自动检测过期的元素, 所以我们就有必要自己
     * 启动一个线程来检测出过期的数据然后使其从缓存里被删除.
     * @param cacheManager
     * @param cacheName
     */
    public static void checkExpiration(CacheManager cacheManager, String cacheName) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                Cache cache = (Cache) cacheManager.getCache(cacheName);
                cache.get("username"); // 只有 password 会过期, 因为 username 的使用时间一直在更新
                cache.evictExpiredElements(); // 过期的元素会被自动从缓存删除
                System.out.printf("Size of cache %s: %d\n", cache.getName(), cache.getSize());
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    public static void main(String[] args) {
        URL url = EhcacheWithListener.class.getResource("/ehcache.xml");
        CacheManager cacheManager = CacheManager.create(url);

        useConfiguredCache(cacheManager, "fox");
        checkExpiration(cacheManager, "fox");

        // cacheManager.shutdown();
    }
}
