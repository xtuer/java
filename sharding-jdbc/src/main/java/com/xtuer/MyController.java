package com.xtuer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.*;

@RestController
public class MyController {
    @Autowired
    private DataSource dataSource;

    /**
     * 创建用户
     *
     * 网址: http://localhost:8080/api/users
     * 参数:
     *      userId  : 用户 ID
     *      username: 用户名
     *
     * @param user 用户
     * @return 成功时返回
     */
    @PostMapping("/api/users")
    public User insertUser(User user) throws SQLException {
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO user (user_id, username) VALUES(?, ?)");
            stmt.setLong(1, user.getUserId());
            stmt.setString(2, user.getUsername());
            stmt.execute();
        }

        return user;
    }

    /**
     * 查询指定 ID 的用户
     *
     * 网址: http://localhost:8080/api/users/{userId}
     * 参数: 无
     *
     * @param userId 用户 ID
     * @return 返回查询到的用户，查询不到返回空字符串
     */
    @GetMapping("/api/users/{userId}")
    public String findUserById(@PathVariable long userId) throws SQLException {
        StringBuilder sb = new StringBuilder();

        try (Connection conn = dataSource.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT user_id AS userId, username FROM user WHERE user_id = " + userId);

            while (rs.next()) {
                sb.append(rs.getLong(1)).append(", ").append(rs.getString(2));
            }
        }

        return sb.toString();
    }
}
