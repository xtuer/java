import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.UUID;

public class Hz {
    private static final String ACCOUNT = "18001166440";

    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("redis.xml");
        RedisTemplate redisTemplate = context.getBean("redisTemplate", RedisTemplate.class);

        String name   = "test1";
        String examId = "50";
        String url    = urlForHz(redisTemplate, name, examId);

        System.out.println(url);
    }

    /**
     * 获取学员在弘智题库的 url
     *
     * @param name 学员名字
     * @param examId 试题库的 id
     * @return
     */
    public static String urlForHz(RedisTemplate redisTemplate, String name, String examId) {
        String md5  = keyForHz(name);
        String uuid = getUuid(redisTemplate, name);
        return String.format("http://hx.ztiku.com/z/hxpoint.aspx?n=%s&g=%s&key=%s&uuid=%s&url=http://www.edu-edu.com", name, examId, md5, uuid);
    }

    /**
     * 获取学员在弘智题库的 key，格式为 "登陆账号+管理账号+时间" 的 md5 编码
     *
     * @param name 学员名字
     * @return 智题库的 key
     */
    public static String keyForHz(String name) {
        String date = DateFormatUtils.format(new Date(), "yyyyMMdd");
        return DigestUtils.md5Hex(name + ACCOUNT + date);
    }

    /**
     * 从 Redis 中获取学员的 UUID，如果不存在则创建并保存到 Redis，每个学员的 uuid 都是唯一，且不变的
     *
     * @param redisTemplate
     * @param name 学员名字
     * @return 学员唯一的 uuid
     */
    public static String getUuid(RedisTemplate redisTemplate, String name) {
        Object uuidObject = redisTemplate.opsForValue().get("hz_" + name);

        if (uuidObject == null) {
            String uuid = UUID.randomUUID().toString().toUpperCase().replace("-", "");
            redisTemplate.opsForValue().set("hz_" + name, uuid);

            return uuid;
        } else {
            return uuidObject.toString();
        }
    }
}
