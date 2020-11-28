package com.xtuer.controller;

import com.xtuer.bean.Page;
import com.xtuer.bean.Result;
import com.xtuer.bean.Urls;
import com.xtuer.bean.User;
import com.xtuer.bean.stock.StockRecord;
import com.xtuer.bean.stock.StockRecordFilter;
import com.xtuer.mapper.StockMapper;
import com.xtuer.service.StockService;
import com.xtuer.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 库存的控制器
 */
@RestController
public class StockController extends BaseController {
    @Autowired
    private StockService stockService;

    @Autowired
    private StockMapper stockMapper;

    /**
     * 查询库存操作记录
     *
     * 网址: http://localhost:8080/api/stocks/records?type=IN
     * 参数:
     *      type         (必要): IN (入库)、OUT (出库)
     *      name         [可选]: 名字
     *      code         [可选]: 编码
     *      batch        [可选]: 批次
     *      model        [可选]: 规格型号
     *      manufacturer [可选]: 厂家
     *      startAt      [可选]: 开始时间
     *      endAt        [可选]: 结束时间
     *      pageNumber   [可选]: 页码
     *      pageSize     [可选]: 数量
     *
     * @param filter 过滤条件
     * @param page   分页对象
     * @return payload 为库存操作记录的数组
     */
    @GetMapping(Urls.API_STOCKS_RECORDS)
    public Result<List<StockRecord>> findStockRecords(StockRecordFilter filter, Page page) {
        // 设置查询时间范围
        filter.setStartAt(Utils.startOfDay(filter.getStartAt()));
        filter.setEndAt(Utils.endOfDay(filter.getEndAt()));

        return Result.ok(stockMapper.findStockRecords(filter, page));
    }

    /**
     * 入库
     *
     * 网址: http://localhost:8080/api/stocks/in
     * 参数:
     *      productItemId (必要): 物料 ID
     *      count         (必要): 入库数量
     *      batch         (必要): 批次
     *      warehouse     [可选]: 仓库
     *
     * @param record 库存操作记录
     * @return payload 为更新后的入库记录
     */
    @PostMapping(Urls.API_STOCKS_IN)
    public Result<StockRecord> stockIn(StockRecord record) {
        User user = super.getCurrentUser();
        return stockService.stockIn(record, user);
    }
}
