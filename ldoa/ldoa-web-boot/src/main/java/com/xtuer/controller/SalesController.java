package com.xtuer.controller;

import com.xtuer.bean.Result;
import com.xtuer.bean.Urls;
import com.xtuer.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

/**
 * 销售管理的控制器
 */
@RestController
public class SalesController extends BaseController {
    @Autowired
    private CustomerService customerService;

    /**
     * 导入客户
     *
     * 网址: http://localhost:8080/api/sales/customers
     * 参数: tempFileUrl 客户信息临时文件的 URL
     *
     * @param tempFileUrl 客户信息临时文件的 URL
     */
    @PutMapping(Urls.API_SALES_CUSTOMERS)
    public Result<Boolean> importCustomers(@RequestParam String tempFileUrl) {
        File tempFile = super.tempFileService.getTempFileByUrl(tempFileUrl);
        customerService.importCustomers(tempFile);

        return Result.ok();
    }
}
