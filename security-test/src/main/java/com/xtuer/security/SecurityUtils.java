package com.xtuer.util;

import com.xtuer.bean.User;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {
    /**
     * 获取登陆用户的信息
     *
     * @return 返回登陆的用户，如果没有登陆则返回 null
     */
    public static User getLoginUser() {
        Object p = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return p instanceof User ? (User) p : null;
    }
}
