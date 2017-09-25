import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.util.DigestUtils;

import java.util.Base64;
import java.util.Map;
import java.util.TreeMap;

public class JwtUtils {
    private static final String APP_SECRET = "App Secret";
    private static final long DURATION     = 3600L * 24 * 30 * 1000; // 30 天

    /**
     * 使用 User 生成 JWT token。
     * 不需要使用 User 的所有信息参与生成 token，可以选择一些关键的，例如用户名，用户 ID。
     *
     * @param user
     * @return token 字符串
     */
    public static String generateToken(User user) {
        Map<String, String> params = new TreeMap<>();
        params.put("id",       user.getId() + "");
        params.put("username", user.getUsername());
        params.put("signAt",   System.currentTimeMillis() + ""); // token 生成时间

        // 签名字符串
        String signString = sign(params, APP_SECRET);

        // 添加其他不参与签名的数据，例如邮件地址
        params.put("email", user.getEmail());
        params.put("roles", JSON.toJSONString(user.getRoles()));
        String payload = JSON.toJSONString(params);

        return base64(payload) + "." + signString;
    }

    /**
     * 检查 token 是否有效:
     *     1. token 的格式匹配 xxxxx.xxxxx
     *     2. token 的 signAt 必须在有效期内，否则无效
     *     3. 使用 token 中对应的信息进行签名计算，计算出的签名和 token 中的签名相等则签名有效
     * @param token
     * @return token 有效时返回 true，无效时返回 false
     */
    public static boolean checkToken(String token) {
        if (token == null || !token.matches("[\\w=]+\\.[\\w=]+")) {
            return false;
        }

        try {
            int pos = token.indexOf(".");
            String payload = unbase64(token.substring(0, pos));
            String signString = token.substring(pos+1);
            JSONObject json = JSON.parseObject(payload);

            // 检查签名的有效期: (currentTime - signAt) > DURATION 时无效
            long signAt = json.getLongValue("signAt");
            long elapsed = System.currentTimeMillis() - signAt;
            if (elapsed > DURATION) {
                return false;
            }

            Map<String, String> params = new TreeMap<>();
            params.put("id",       json.getString("id"));
            params.put("username", json.getString("username"));
            params.put("signAt",   json.getString("signAt"));

            String calculatedSignString = sign(params, APP_SECRET);

            return signString.equals(calculatedSignString);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return false;
    }

    /**
     * 从 token 中提取出用户信息
     *
     * @param token
     * @return token 有效时返回 User 对象，否则返回 null
     */
    public static User extractUser(String token) {
        if (!checkToken(token)) {
            return null;
        }

        try {
            int pos = token.indexOf(".");
            String payload = unbase64(token.substring(0, pos));
            JSONObject json = JSON.parseObject(payload);
            JSONArray roles = JSON.parseArray(json.getString("roles"));
            json.put("roles", roles); // 注意: json 中的 roles 是字符串，需要转换为数组

            return JSON.parseObject(json.toJSONString(), User.class);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return null;
    }

    public static String sign(Map<String, String> params, String secret) {
        return md5(JSON.toJSONString(params) + secret);
    }

    public static String md5(String text) {
        return DigestUtils.md5DigestAsHex(text.getBytes());
    }

    public static String base64(String text) {
        return Base64.getEncoder().encodeToString(text.getBytes());
    }

    public static String unbase64(String base64Text) {
        return new String(Base64.getDecoder().decode(base64Text));
    }

    public static void main(String[] args) {
        User user = new User(1234, "Biao", "biao.mac@icloud.com", "ROLE_ADMIN", "ROLE_USER");

        String token = generateToken(user);
        System.out.println(token);

        user = extractUser(token);
        System.out.println(JSON.toJSONString(user));
        System.out.println(user.getRoles().size());
    }
}
