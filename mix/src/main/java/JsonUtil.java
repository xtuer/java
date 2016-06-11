import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

public class JsonUtil {
    private static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // 不序列化 null
    }

    public static String toJson(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void main(String[] args) throws IOException {
        User user = new User(1, "Alice", "alice@gmail.com");

        String json = JsonUtil.toJson(user);
        System.out.println(json);

        user = JsonUtil.fromJson(json, User.class);
        System.out.printf("id: %d, username: %s, email: %s\n", user.getId(), user.getUsername(), user.getEmail());

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = null;

        // 1. map 中的类型是 built-in 的类型
        map = mapper.readValue(json, Map.class);
        System.out.println(map);

        // 2. map 中的类型可以是自己定义的类型
        map = mapper.readValue(json,  new TypeReference<Map<String, String>>(){});
        System.out.println(map);
    }
}
