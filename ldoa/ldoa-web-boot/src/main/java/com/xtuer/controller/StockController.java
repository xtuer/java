package com.xtuer.controller;

import com.xtuer.bean.Result;
import com.xtuer.bean.Urls;
import com.xtuer.bean.User;
import com.xtuer.bean.stock.StockRecord;
import com.xtuer.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 库存的控制器
 */
@RestController
public class StockController extends BaseController {
    @Autowired
    private StockService stockService;

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
     */
    @PostMapping(Urls.API_STOCKS_IN)
    public Result<StockRecord> stockIn(StockRecord record) {
        User user = super.getCurrentUser();
        return stockService.stockIn(record, user);
    }
}
