package com.xtuer.mapper;

import com.xtuer.bean.stock.StockRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 库存的 Mapper
 */
@Mapper
public interface StockMapper {
    /**
     * 创建库存操作记录
     *
     * @param record 库存记录
     */
    void insertStockRecord(StockRecord record);

    /**
     * 更新或者创建库存
     *
     * @param productItemId 物料 ID
     * @param count         数量
     */
    void upsertStock(long productItemId, int count);
}
