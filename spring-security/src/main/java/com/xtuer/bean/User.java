package com.xtuer.bean;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.*;

/**
 * 用户类型，根据 userdetails.User 的设计，roles, authorities, enabled, expired 等状态不能修改，
 * 只能是创建用户对象的时候传入进来。
 */
@Getter
@Setter
public class User extends org.springframework.security.core.userdetails.User {
    private Long   id;
    private String username;
    private String password;
    private String mail;
    private Object data; // 可以存储其他数据，这里只是为了展示一个占位符而已

    public User() {
        // 父类不允许空的用户名、密码和权限，所以给个默认的，这样就可以用默认的构造函数创建 User 对象。
        super("non-exist-username", "", new HashSet<>());
    }

    /**
     * 使用账号、密码、角色创建用户
     *
     * @param username 账号
     * @param password 密码
     * @param roles    角色
     */
    public User(String username, String password, String... roles) {
        this(username, password, true, roles);
    }

    /**
     * 使用账号、密码、是否禁用、角色创建用户
     *
     * @param username 账号
     * @param password 密码
     * @param enabled  是否禁用
     * @param roles    角色
     */
    public User(String username, String password, boolean enabled, String... roles) {
        super(username, password, enabled, true, true, true, AuthorityUtils.createAuthorityList(roles));
        this.username = username;
        this.password = password;
    }

    /**
     * 获取用户的角色，只是为了使用方便
     *
     * @return 角色的集合
     */
    public Collection<String> getRoles() {
        Collection<GrantedAuthority> authorities = super.getAuthorities();
        Set<String> roles = new HashSet<>();

        for (GrantedAuthority authority : authorities) {
            roles.add(authority.getAuthority());
        }

        return Collections.unmodifiableSet(roles);
    }

    public static void main(String[] args) {
        User user1 = new User();
        System.out.println(JSON.toJSONString(user1));
        System.out.println(user1.getRoles());

        User user2 = new User("Bob", "Passw0rd", "ROLE_USER", "ROLE_ADMIN");
        System.out.println(JSON.toJSONString(user2));
        System.out.println(user2.getRoles());
    }
}
