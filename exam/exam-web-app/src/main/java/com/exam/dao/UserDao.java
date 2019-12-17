package com.exam.dao;

import com.exam.bean.User;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 用户 Dao (MongoDB 使用)
 */
@Service
public class UserDao {
    private static final String LOGIN  = "user_login";

    @Resource(name = "mongoTemplate")
    private MongoTemplate mongoTemplate;

    /**
     * 创建用户登录记录
     *
     * @param user 用户
     */
    public void insertUserLoginRecord(@NotNull User user) {
        Document document = new Document()
                .append("orgId", user.getOrgId())
                .append("userId", user.getId())
                .append("username", user.getUsername())
                .append("nickname", user.getNickname())
                .append("createdAt", new Date());

        mongoTemplate.insert(document, LOGIN);
    }
}
