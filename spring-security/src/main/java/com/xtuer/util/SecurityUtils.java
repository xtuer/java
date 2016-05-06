package com.xtuer.util;

import com.xtuer.bean.MyUserDetails;
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

    /**
     * 取得登陆用户的 ID, 如果没有登陆则返回 -1
     * @return 登陆用户的 ID
     */
    public static int getLoginUserId() {
        Object principle = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (SecurityUtils.isLogin()) {
            MyUserDetails userDetails = (MyUserDetails) principle;
            return userDetails.getUserId();
        }

        return -1;
    }
}
