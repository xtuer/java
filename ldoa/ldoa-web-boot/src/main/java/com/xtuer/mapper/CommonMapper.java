package com.xtuer.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

/**
 * 通用 Mapper
 */
@Mapper
public interface CommonMapper {
    /**
     * 查询序列号
     *
     * @param sequenceName 序列号名字
     * @return 返回查询到的序列号
     */
    int findSequenceByName(String sequenceName);

    /**
     * 获得序列化的锁
     *
     * @param sequenceName 序列号名字
     * @return 返回查询到的序列号
     */
    Integer lockSequenceByName(String sequenceName);

    /**
     * 创建序列号
     *
     * @param sequenceName 序列号名字
     * @param value 初始序列号的值
     */
    void createSequence(String sequenceName, int value);

    /**
     * 序列号增加 1
     *
     * @param sequenceName 序列号名字
     */
    void increaseSequenceByOne(String sequenceName);

    /**
     * 测试 XA 命令
     */
    @SuppressWarnings("rawtypes")
    Map xaTest();
}
