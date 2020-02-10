package com.xtuer.controller;

import com.xtuer.bean.Result;
import com.xtuer.bean.Spare;
import com.xtuer.bean.Urls;
import com.xtuer.mapper.SpareMapper;
import com.xtuer.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 备件控制器
 */
@RestController
public class SpareController extends BaseController {
    @Autowired
    private SpareMapper spareMapper;

    /**
     * 查询符合条件的备件
     *
     * 网址: http://localhost:8080/api/spares
     * 参数: filter 可包含下面几个属性
     *      pageSize   [可选]: 数量，如无则由服务器端决定
     *      pageNumber [可选]: 页码，如无则默认为 1
     *
     * @param filter 过滤条件
     * @return payload 为备件数组
     */
    @GetMapping(Urls.API_SPARES)
    public Result<List<Spare>> findSpares(Spare filter) {
        return Result.ok(spareMapper.findSpares(filter));
    }

    /**
     * 创建或者更新备件
     *
     * 网址: http://localhost:8080/api/spares/{spareId}
     * 参数: 备件属性，参考 SpareUtils.newSpare() 返回的备件对象的属性
     *
     * @param spare 订单
     * @return payload 为备件 ID
     */
    @PutMapping(Urls.API_SPARES_BY_ID)
    public Result<Long> upsertSpare(Spare spare) {
        if (Utils.isInvalidId(spare.getId())) {
            spare.setId(super.nextId());
        }

        spareMapper.upsertSpare(spare);
        return Result.ok(spare.getId());
    }

    /**
     * 删除指定 ID 的备件
     *
     * 网址: http://localhost:8080/api/spares/{spareId}
     * 参数: 无
     *
     * @param spareId 备件 ID
     */
    @DeleteMapping(Urls.API_SPARES_BY_ID)
    public Result<Boolean> deleteSpare(@PathVariable long spareId) {
        spareMapper.deleteSpare(spareId);
        return Result.ok();
    }
}
