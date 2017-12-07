package com.xtuer.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xtuer.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.TreeMap;

/**
 * 使用 JWT 的算法生成 token、验证 token 的有效性以及从 token 中提取数据。Token 中包含了用户数据、签名，并且能够防止 token 被篡改。
 * 增加或者删除不参与签名的数据已有 token 不会失效，增加或者删除参与签名的数据会使已有 token 失效.
 *
 * 标准 JWT 生成的 token 由 3 部分组成，这里对其进行了简化，去掉了算法说明的部分，保留了数据和签名部分.
 * 参考: http://www.jianshu.com/p/576dbf44b2ae
 *
 * 需要注意的是，放到 token 里的数据不要太多，否则会使得 token 很大，而 token 有可能放在 cookie, header 中，
 * 如果过大，容易被截断导致 token 无效.
 */
public class JwtUtils {
    private static Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    /**
     * 使用 User 生成 token，由 2 部分组成，第一个部分为 payload 进行 Base64 编码的字符串，第二部分为签名.
     *
     * 算法:
     *     payload: 用户信息 + 签名生成时间进行 Base64 编码
     *     签名: 用户的关键信息(例如用户名，角色) + 签名生成时间 + 应用的秘钥使用 MD5 生成签名
     *
     * @param user 用户对象
     * @param secret 签名的秘钥匙
     * @return token 字符串
     */
    public static String generateToken(User user, String secret) {
        // 参与签名的数据: id, username, roles, signAt
        Map<String, String> params = new TreeMap<>(); // 使用 TreeMap 是为了 key 能够按照字母序进行排序
        params.put("id",       user.getId() + "");
        params.put("username", user.getUsername());
        params.put("roles",    JSON.toJSONString(user.getRoles()));
        params.put("signAt",   System.currentTimeMillis() + ""); // token 生成时间

        // 签名
        String signString = sign(params, secret);

        // 添加其他不参与签名的数据，例如邮件地址
        params.put("mail", user.getMail());
        String payload = JSON.toJSONString(params);

        // 使用 Url Safe Base64 进行编码是因为等号 = 会影响读取 cookie 中的值
        // 而 token 有可能放在 cookie, header, url 中
        return CommonUtils.base64UrlSafe(payload) + "." + signString;
    }

    /**
     * 检测 token 是否有效:
     *     1. token 的格式匹配 xxxxx.xxxxx
     *     2. token 的 signAt 需要在有效期内，否则无效
     *     3. 根据签名算法进行签名，计算出的签名和 token 中的签名相等则签名有效
     * @param token JWT token 字符串
     * @param secret 签名的秘钥匙
     * @param duration token 的有效期
     * @return token 有效时返回 true，无效时返回 false
     */
    public static boolean checkToken(String token, String secret, long duration) {
        // token 的格式: xxxxx.xxxxx，包含 0-9, a-z, A-Z, %
        if (token == null || !token.matches("[\\w%]+\\.[\\w]+")) {
            logger.debug("Token 格式不对: {}", token);
            return false;
        }

        try {
            // 从 token 中得到用户信息的字符串和签名字符串
            int pos = token.indexOf(".");
            String payload = CommonUtils.unbase64UrlSafe(token.substring(0, pos));
            String signString = token.substring(pos+1);
            JSONObject json = JSON.parseObject(payload);

            // 检查签名的有效期: (currentTime - signAt) > DURATION 时无效
            long signAt = json.getLongValue("signAt");
            long elapsed = System.currentTimeMillis() - signAt;
            if (elapsed > duration) {
                logger.debug("Token 过期: {}", token);
                return false;
            }

            // 使用 token 中的 id, username, roles, signAt 计算签名
            Map<String, String> params = new TreeMap<>();
            params.put("id",       json.getString("id"));
            params.put("username", json.getString("username"));
            params.put("roles",    json.getString("roles"));
            params.put("signAt",   json.getString("signAt"));

            logger.debug("用户: {}", JSON.toJSONString(params));

            // 如果相等则签名没问题，不相等则签名被篡改，token 无效
            String calculatedSignString = sign(params, secret);
            return signString.equals(calculatedSignString);
        } catch (Exception ex) {
            logger.warn("Token 无效，不能转换为 User 对象: {}", token); // JSON 转换可能出错
        }

        return false;
    }

    /**
     * 从 token 中提取用户信息，如果 token 无效则返回 null.
     *
     * @param token JWT 生成的 token
     * @param secret 签名的秘钥匙
     * @param duration token 的有效期
     * @return 返回 token 中的用户对象，如果 token 无效则返回 null
     */
    public static User extractUser(String token, String secret, long duration) {
        if (!checkToken(token, secret, duration)) {
            return null;
        }

        try {
            int pos = token.indexOf(".");
            String payload = CommonUtils.unbase64UrlSafe(token.substring(0, pos));
            JSONObject json = JSON.parseObject(payload);
            JSONArray roles = JSON.parseArray(json.getString("roles"));
            json.put("roles", roles); // 注意: json 中的 roles 是字符串，需要转换为数组

            return JSON.parseObject(json.toJSONString(), User.class);
        } catch (Exception ex) {
            logger.warn("Token 无效，不能转换为 User 对象: {}", token); // JSON 转换可能出错
        }

        return null;
    }

    /**
     * 签名计算
     */
    public static String sign(Map<String, String> params, String secret) {
        return CommonUtils.md5(JSON.toJSONString(params) + secret);
    }
}
