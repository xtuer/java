import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class Test {
    private static SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);

    public static void main(String[] args) throws InterruptedException {
        User user = new User();
        user.setId(idWorker.nextId());
        user.setUsername("黄彪");

        // String json = JSON.toJSONString(user, SerializerFeature.BrowserCompatible);
        // System.out.println(json);

        String json = "{\"id\":\"143972849483251712\",\"last\":false,\"roles\":[],\"username\":\"\\u9EC4\\u5F6A\"}";
        user = JSON.parseObject(json, User.class);
        System.out.println(user);
    }
}
