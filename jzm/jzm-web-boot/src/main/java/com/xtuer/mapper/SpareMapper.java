package com.xtuer.mapper;

import com.xtuer.bean.Page;
import com.xtuer.bean.Spare;
import com.xtuer.bean.SpareWarehousingLog;
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
     * 查询指定 ID 的备件
     *
     * @param spareId 备件 ID
     * @return 返回查询到的备件，否则返回 null
     */
    Spare findSpareById(long spareId);

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

    /**
     * 更新芯片库存
     *
     * @param spareId      备件 ID
     * @param chipQuantity 芯片数量
     */
    void updateChipQuantity(long spareId, int chipQuantity);

    /**
     * 查询符合条件的库存日志
     *
     * @return 返回库存日志的数组
     */
    List<SpareWarehousingLog> findSpareWarehousingLogs(Page page);

    /**
     * 插入出入库操作日志
     *
     * @param log 出入库日志
     */
    void insertSpareWarehousingLog(SpareWarehousingLog log);
}
