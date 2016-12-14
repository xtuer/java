import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.UUID;

public class Hz {
    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("redis.xml");
        final RedisTemplate redisTemplate = context.getBean("redisTemplate", RedisTemplate.class);

        String name = "test1";
        String uuid = getUuid(redisTemplate, name);
        String examId = "50";
        System.out.println(urlForHz(name, examId, uuid));
    }

    public static String getUuid(RedisTemplate redisTemplate, String name) {
        Object uuidObject = redisTemplate.opsForValue().get("HZ_" + name);

        if (uuidObject == null) {
            String uuid = UUID.randomUUID().toString().toUpperCase().replace("-", "");
            redisTemplate.opsForValue().set("HZ_" + name, uuid);

            return uuid;
        } else {
            return uuidObject.toString();
        }
    }

    public static String urlForHz(String name, String examId, String uuid) {
        String md5 = md5ForHz(name);
        return String.format("http://hx.ztiku.com/z/hxpoint.aspx?n=%s&g=%s&key=%s&uuid=%s&url=http://www.edu-edu.com", name, examId, md5, uuid);
    }

    public static String md5ForHz(String name) {
        String date = DateFormatUtils.format(new Date(), "yyyyMMdd");
        return DigestUtils.md5Hex(name + "18001166440" + date);
    }
}
