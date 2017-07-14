import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Sign {
    public static void main(String[] args) throws IOException {
        String appId = "015B512C873648578FB2C32BD5677BD4";
        String appKey = "927170905ECA42FC9813DD7EED21A5AF";

        // 参与签名的参数
        Map<String, String> params = new HashMap<>();
        params.put("app_id", appId);
        params.put("app_key", appKey);
        params.put("username", "alice");
        params.put("productId", "1001");
        params.put("signedTime", "1499914521231");

        String signValue = sign(params);
        System.out.println(signValue); // 281879C9007C3698D1106F9CF6A097A3
    }

    public static String sign(Map<String, String> params) {
        Map<String, String> temp = new TreeMap<>(params); // 对参数进行排序

        // 拼接参数
        StringBuilder sb = new StringBuilder();
        for (String key : temp.keySet()) {
            sb.append(key).append("=").append(temp.get(key)).append("&");
        }

        String signTemp = sb.deleteCharAt(sb.length() - 1).toString(); // 去掉最后一个 &
        String signValue = DigestUtils.md5DigestAsHex(signTemp.getBytes()).toUpperCase(); // 使用 MD5 计算签名字符串

        return signValue;
    }
}
