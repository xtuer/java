package com.xtuer.security;

import com.xtuer.bean.User;
import com.xtuer.bean.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 自定义的 UserDetails 可以保存用户的其他信息到 session 里, 例如 user id 等.
 */
public class MyUserDetails extends org.springframework.security.core.userdetails.User {
    private int userId;

    public MyUserDetails(User user) {
        super(user.getUsername(), user.getPassword(), user.isEnabled(), true, true, true, buildUserAuthorities(user));
        this.userId = user.getId();
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * 把用户的权限 UserRole 转换成 GrantedAuthority
     * @param user 用户
     * @return
     */
    private static List<GrantedAuthority> buildUserAuthorities(User user) {
        Set<UserRole> userRoles = user.getUserRoles();
        Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();

        // Build user's authorities
        for (UserRole userRole : userRoles) {
            authorities.add(new SimpleGrantedAuthority(userRole.getRole()));
        }

        return new ArrayList<GrantedAuthority>(authorities);
    }
}
