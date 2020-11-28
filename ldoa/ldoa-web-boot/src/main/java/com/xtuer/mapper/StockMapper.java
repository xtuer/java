package com.xtuer.mapper;

import com.xtuer.bean.Page;
import com.xtuer.bean.stock.StockRecord;
import com.xtuer.bean.stock.StockRecordFilter;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 库存的 Mapper
 */
@Mapper
public interface StockMapper {
    /**
     * 查询库存操作记录
     *
     * @param filter 库存操作类型
     * @param page   分页对象
     * @return 返回库存操作记录的数组，如果没有记录则返回空的 list
     */
    List<StockRecord> findStockRecords(StockRecordFilter filter, Page page);

    /**
     * 查询指定 ID 的库存记录
     *
     * @param recordId 库存记录 ID
     * @return 返回查询到的库存记录，查询不到返回 null
     */
    StockRecord findStockRecordById(long recordId);

    /**
     * 创建库存操作记录
     *
     * @param record 库存记录
     */
    void insertStockRecord(StockRecord record);
}
