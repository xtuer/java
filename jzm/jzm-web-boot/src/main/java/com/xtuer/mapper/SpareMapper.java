package com.xtuer.mapper;

import com.xtuer.bean.Spare;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 备件 Mapper
 */
@Mapper
public interface SpareMapper {
    /**
     * 查询符合条件的备件
     *
     * @param filter 过滤条件
     * @return 返回查询到的备件数组
     */
    List<Spare> findSpares(Spare filter);

    /**
     * 创建或者更新备件
     *
     * @param spare 备件
     */
    void upsertSpare(Spare spare);

    /**
     * 删除指定 ID 的备件
     *
     * @param spareId 备件 ID
     */
    void deleteSpare(long spareId);
}
