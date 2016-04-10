package com.xtuer.dao;

import com.xtuer.domain.User;
import com.xtuer.domain.UserRole;

import java.util.*;

public class UserDao {
    private static Map<String, User> users = new HashMap<String, User>();

    static {
        // 模拟数据源，可以是多种，如数据库，LDAP，从配置文件读取等
        UserRole userRole = new UserRole("ROLE_USER");   // 普通用户权限
        UserRole adminRole = new UserRole("ROLE_ADMIN"); // 管理员权限

        // Passw0rd 的 BCrypt 加密结果
        String password = "$2a$10$gtaxGaHMfxMRj6rqK/kp0.5TPF13CBvnXhvD7teUmeftH1cX0Mb6S";

        users.put("admin", new User("admin", password, true, new HashSet<UserRole>(Arrays.asList(adminRole))));
        users.put("alice", new User("alice", password, true, new HashSet<UserRole>(Arrays.asList(userRole))));
    }

    public User findUserByUsername(String username) {
        return users.get(username);
    }
}
