package com.xtuer.bean;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class User extends org.springframework.security.core.userdetails.User {
    private Long id;
    private String username;
    private String password;
    private Set<String> roles = new HashSet<>();

    public User() {
        // 父类不允许空的用户名、密码和权限，所以给个默认的，这样就可以用默认的构造函数创建 User 对象。
        super("non-exist-username", "", new HashSet<GrantedAuthority>());
    }

    public User(String username, String password, String... roles) {
        super(username, password, buildAuthorities(roles));
        this.username = username;
        this.password = password;
        this.roles.addAll(Arrays.asList(roles));
    }

    /**
     * 使用 new User() 创建的对象，或者 username、password 和 roles 改变后没有在父类中更新对应数据，
     * 可以使用这个方法重新构造一个正确的 User 对象，这个对象是为了和 Spring Security 一起使用。
     */
    public static User userWithAuthorities(User user) {
        return new User(user.getUsername(), user.getPassword(), user.getRoles().toArray(new String[0]));
    }

    /**
     * 使用字符串数组的 roles 创建 GrantedAuthority 的 Set。
     */
    public static Set<GrantedAuthority> buildAuthorities(String... roles) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        for (String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return authorities;
    }
}
