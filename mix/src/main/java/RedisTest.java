import com.xtuer.RedisUserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RedisTest {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("redis-cache.xml");
        RedisUserService service = context.getBean("redisUserService", RedisUserService.class);
        System.out.println(service.findUserById(123)); // 创建对象, 生成缓存
        System.out.println(service.findUserById(123)); // 直接从缓存读取
        service.deleteUser(123); // 删除缓存
    }
}
