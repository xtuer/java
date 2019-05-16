import org.junit.runner.RunWith;
import org.openqa.selenium.support.CacheLookup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.junit.Test;

@RunWith(SpringRunner.class)
@ContextConfiguration({"classpath:caffeine.xml"})
public class CacheTest {
    @Autowired
    private CacheManager cacheManager;

    @Test
    public void cacheTest() {
        System.out.println(cacheManager.getCacheNames());
    }
}
