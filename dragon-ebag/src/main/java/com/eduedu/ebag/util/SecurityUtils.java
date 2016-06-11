package com.eduedu.ebag.util;

import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    /**
     * 判断当前用户是否已经登陆
     * @return 登陆状态返回 true, 否则返回 false
     */
    public static boolean isLogin() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return !"anonymousUser".equals(username);
    }

    public static int getLoginUserId() {
        return 10;
    }
}
