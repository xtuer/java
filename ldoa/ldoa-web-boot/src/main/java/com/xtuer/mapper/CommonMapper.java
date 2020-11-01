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
     * 序列号增加 1
     *
     * @param sequenceName 序列号名字
     */
    void increaseSequenceByName(String sequenceName);

    /**
     * 测试 XA 命令
     */
    @SuppressWarnings("rawtypes")
    Map xaTest();
}
