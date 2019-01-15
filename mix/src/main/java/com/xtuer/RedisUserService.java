package com.xtuer;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class RedisUserService {
    /**
     * 第一次查询时从数据库读取, 并保存到 Redis.
     *
     * value 为 Redis 键的前缀, 不能为空
     * key   为 Redis 键的动态部分, 由传入的参数确定, 使用
     *       Spring Expression Language (SpEL) expression for computing the key dynamically.
     *
     * 调用函数 findUserById(123) Redis 中生成缓存的 key 为: user:id_123
     */
    @Cacheable(value="user", key="'id_' + #id")
    public String findUserById(int id) {
        System.out.println("Find user: " + id);
        return "User_" + id;
    }

    /**
     * 删除用户, 并删除 Redis 中的的缓存
     */
    @CacheEvict(value="user", key="'id_' + #id")
    public void deleteUser(int id) {
        System.out.println("Delete user: " + id);
    }
}
