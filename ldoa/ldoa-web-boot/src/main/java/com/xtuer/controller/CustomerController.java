package com.xtuer.controller;

import com.xtuer.bean.Page;
import com.xtuer.bean.Result;
import com.xtuer.bean.Urls;
import com.xtuer.bean.sales.Customer;
import com.xtuer.bean.sales.CustomerFilter;
import com.xtuer.mapper.CustomerMapper;
import com.xtuer.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;

/**
 * 客户的控制器
 */
@RestController
public class CustomerController extends BaseController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerMapper customerMapper;

    /**
     * 查询客户
     *
     * 网址: http://localhost:8080/api/sales/customers
     * 参数: 无
     *
     * @return payload 为客户的数组
     */
    @GetMapping(Urls.API_SALES_CUSTOMERS)
    public Result<List<Customer>> findCustomers(CustomerFilter filter, Page page) {
        return Result.ok(customerMapper.findCustomers(filter, page));
    }

    /**
     * 导入客户
     *
     * 网址: http://localhost:8080/api/sales/customers/import
     * 参数: tempFileUrl 客户信息临时文件的 URL
     *
     * @param tempFileUrl 客户信息临时文件的 URL
     */
    @PutMapping(Urls.API_SALES_CUSTOMERS_IMPORT)
    public Result<Boolean> importCustomers(@RequestParam String tempFileUrl) {
        File tempFile = super.tempFileService.getTempFileByUrl(tempFileUrl);
        customerService.importCustomers(tempFile);

        return Result.ok();
    }

    /**
     * 删除指定 ID 的客户
     *
     * 网址: http://localhost:8080/api/sales/customers/{customerId}
     * 参数: 无
     *
     * @param customerId 客户 ID
     */
    @DeleteMapping(Urls.API_SALES_CUSTOMERS_BY_ID)
    public Result<Boolean> deleteCustomer(@PathVariable long customerId) {
        customerMapper.deleteCustomer(customerId);
        return Result.ok();
    }
}
