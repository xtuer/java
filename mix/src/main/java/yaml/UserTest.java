package yaml;

import com.alibaba.fastjson.JSON;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;

public class UserTest {
    public static void main(String[] args) {
        unmarshalUserTest();
        // unmarshalUserHolderTest();
    }

    public static void unmarshalUserTest() {
        Yaml yaml = new Yaml(new Constructor(User.class));
        InputStream in = ClassLoader.getSystemResourceAsStream("user.yml"); // 找不到返回 null
        User user = yaml.load(in);

        System.out.println(JSON.toJSONString(user));
    }

    public static void unmarshalUserHolderTest() {
        Yaml yaml = new Yaml(new Constructor(UserHolder.class));
        InputStream in = ClassLoader.getSystemResourceAsStream("user-holder.yml");
        UserHolder user = yaml.load(in);

        System.out.println(JSON.toJSONString(user, true));
    }
}
