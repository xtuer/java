package com.xtuer.service;

import com.xtuer.bean.Result;
import com.xtuer.bean.User;
import com.xtuer.bean.stock.StockRecord;
import com.xtuer.mapper.ProductMapper;
import com.xtuer.mapper.StockMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 库存的服务
 */
@Service
@Slf4j
public class StockService extends BaseService {
    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private ProductMapper productMapper;

    /**
     * 入库
     *
     * @param record 库存记录
     * @param user   操作员
     * @return payload 为更新后的操作入库记录
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<StockRecord> stockIn(StockRecord record, User user) {
        // 1. 数据校验
        // 2. 设置入库的相关数据
        // 3. 创建入库记录
        // 4. 入库: 增加产品项的数量

        // [1] 数据校验
        if (record.getCount() <= 0) {
            return Result.fail("入库数量必须大于 0");
        }
        if (StringUtils.isBlank(record.getBatch())) {
            return Result.fail("批次不能为空");
        }

        // [2] 设置入库的相关数据
        record.setStockRecordId(super.nextId());
        record.setType(StockRecord.Type.IN);
        record.setUserId(user.getUserId());
        record.setUsername(user.getNickname());
        record.setStockRequestId(0);
        record.setCompleted(true);

        // [3] 创建入库记录
        // [4] 入库: 增加产品项的数量

        stockMapper.insertStockRecord(record);
        productMapper.increaseProductItemCount(record.getProductItemId(), record.getCount());

        return Result.ok();
    }
}
