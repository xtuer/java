package com.xtuer.service;

import com.xtuer.bean.Result;
import com.xtuer.bean.Spare;
import com.xtuer.bean.SpareWarehousingLog;
import com.xtuer.mapper.SpareMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 备件服务
 */
@Service
public class SpareService {
    @Autowired
    private SpareMapper spareMapper;

    /**
     * 对指定 ID 的备件的芯片进行入库出库
     *
     * @param spareId      备件 ID
     * @param chipQuantity 芯片数量
     * @return payload 为新的芯片数量
     */
    @Transactional(rollbackFor = RuntimeException.class)
    public Result<Integer> warehousing(String username, long spareId, int chipQuantity, Date date) {
        // 1. 查询备件，如果不存在则返回
        // 2. 校验芯片数量
        // 3. 保存操作日志
        // 4. quantity 大于 0 则入库
        // 5. quantity 小于 0 则出库

        // [1] 查询备件，如果不存在则返回
        Spare spare = spareMapper.findSpareById(spareId);
        if (spare == null) {
            return Result.fail("备件不存在: " + spareId);
        }

        // [2] 校验芯片数量
        int oldQuantity = spare.getChipQuantity();
        int newQuantity = oldQuantity + chipQuantity;
        if (newQuantity < 0) {
            return Result.fail("芯片数量不足，出库失败");
        }

        // [3] 保存操作日志
        SpareWarehousingLog log = new SpareWarehousingLog();
        log.setUsername(username)
                .setSpareId(spare.getId())
                .setSpareSn(spare.getSn())
                .setOldChipQuantity(oldQuantity)
                .setNewChipQuantity(newQuantity)
                .setQuantity(chipQuantity)
                .setDate(date);
        spareMapper.insertSpareWarehousingLog(log);

        if (chipQuantity > 0) {
            // [4] quantity 大于 0 则入库
            spareMapper.updateChipQuantity(spareId, newQuantity);
            return Result.ok(newQuantity, "入库成功");
        } else {
            // [5] quantity 小于 0 则出库
            spareMapper.updateChipQuantity(spareId, newQuantity);
            return Result.ok(newQuantity, "出库成功");
        }
    }
}
