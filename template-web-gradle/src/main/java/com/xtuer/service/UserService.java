package com.xtuer.service;

import com.xtuer.bean.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    public User findUserByUsername(String username) {
        if ("admin".equals(username)) {
            return new User("admin", "admin", "ROLE_ADMIN");
        } else if ("alice".equals(username)) {
            return new User("alice", "alice", "ROLE_USER");
        }

        return null;
    }

    public User findUserByUsernamePassword(String username, String password) {
        return findUserByUsername(username); // 测试使用
    }
}
