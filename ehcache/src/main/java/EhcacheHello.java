import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import java.net.URL;

public class EhcacheHello {
    /**
     * 使用 defaultCache 的配置创建缓存
     *
     * @param cacheManager
     */
    public static void createCacheWithDefaultConfiguration(CacheManager cacheManager) {
        cacheManager.addCache("buddha"); // 创建 Cache

        Cache cache = (Cache) cacheManager.getCache("buddha");
        cache.put(new Element("username", "Biao"));
        System.out.println(cache.get("username"));
    }

    /**
     * 获取配置中的缓存
     *
     * @param cacheManager
     * @param cacheName
     */
    public static void useConfiguredCache(CacheManager cacheManager, String cacheName) {
        Cache cache = (Cache) cacheManager.getCache(cacheName); // 获取 Cache

        cache.put(new Element("password", "S--S"));
        cache.put(new Element("username", "Viva"));
        cache.put(new Element("username", "Pandora"));
        System.out.printf("Size of cache %s: %d\n", cacheName, cache.getSize());

        cache.remove("username");
        cache.put(new Element("username", "Viva"));
    }

    public static void main(String[] args) {
        URL url = EhcacheHello.class.getResource("/ehcache.xml");
        CacheManager cacheManager = CacheManager.create(url);

        createCacheWithDefaultConfiguration(cacheManager);
        useConfiguredCache(cacheManager, "fox");

        System.out.println("缓存:");
        for (String name : cacheManager.getCacheNames()) {
            System.out.println(name);
        }

        // cacheManager.shutdown();
    }
}
