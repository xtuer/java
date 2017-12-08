package com.xtuer.utils;

import com.xtuer.bean.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {
    /**
     * 获取登陆用户的信息
     *
     * @return 返回登陆的用户，如果没有登陆则返回 null
     */
    public static User getLoginUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // URL 没有经过 Spring Security 登陆验证的 filter 时 auth 为 null
        if (auth == null) {
            return null;
        }

        Object p = auth.getPrincipal();

        return p instanceof User ? (User) p : null;
    }
}
