package com.xtuer.service;

import com.xtuer.bean.User;

import java.util.HashMap;
import java.util.Map;

public class UserService {
    private static Map<String, User> users = new HashMap<String, User>();

    static {
        // 模拟数据源，可以是多种，如数据库，LDAP，从配置文件读取等
        users.put("admin", new User("admin", "{noop}Passw0rd", "ROLE_ADMIN"));
        users.put("alice", new User("alice", "{bcrypt}$2a$10$dtA5fPvVJEBHLPp7FZci9uKJL90zF8T1EQZzP9qownQlf130bdBZW", "ROLE_USER"));
    }

    public User findUserByUsername(String username) {
        return users.get(username);
    }
}
