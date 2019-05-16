import com.alibaba.fastjson.JSON;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCacheManager;

public class Test {
    public static void main(String[] args) throws InterruptedException {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        Cache cache = cacheManager.getCache("Biao");
        cache.put("time", "1234");
        System.out.println(JSON.toJSONString(cache.get("time").get()));
    }
}
