package com.xtuer.service;

import com.alibaba.fastjson.JSON;
import com.xtuer.bean.User;
import com.xtuer.util.JwtUtils;

public class JwtService {
    private String appSecret = "App secret"; // 应用的秘钥，可以定期更换
    private long tokenDuration = 3600L * 24 * 30 * 1000; // token 有效期为 30 天

    public String generateToken(User user) {
        return JwtUtils.generateToken(user, appSecret);
    }

    public boolean checkToken(String token) {
        return JwtUtils.checkToken(token, appSecret, tokenDuration);
    }

    public User extractUser(String token) {
        return JwtUtils.extractUser(token, appSecret, tokenDuration);
    }

    public static void main(String[] args) {
        JwtService jwt = new JwtService();

        // 创建用户对象
        User user = new User("Biao", "---", "ROLE_ADMIN", "ROLE_STAFF");
        user.setId(1234L);
        user.setMail("biao.mac@icloud.com");

        // 使用 user 生成 token
        String token = jwt.generateToken(user);
        System.out.println(token);

        // 从 token 中提取用户
        user = jwt.extractUser(token);
        System.out.println(JSON.toJSONString(user));
    }
}
