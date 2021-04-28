package com.xtuer.service;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.xtuer.bean.Result;
import com.xtuer.bean.sales.Customer;
import com.xtuer.mapper.CustomerMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;

/**
 * 客户的服务
 */
@Service
public class CustomerService extends BaseService {
    @Autowired
    private CustomerMapper customerMapper;

    public Result<Customer> upsertCustomer(Customer customer) {
        // 1. 数据校验
        // 2. 如果是新建的客户，编码不能为使用过
        // 3. 保存到数据库

        // [1] 数据校验
        // 客户编号没有使用过
        String customerSn = customer.getCustomerSn();
        if (customerMapper.isCustomerSnUsed(customer.getCustomerId(), customerSn)) {
            return Result.fail("客户编号 {} 已经被使用，请换一个新的!", customerSn);
        }

        // [2] 如果是新建的客户，编码不能为使用过
        if (customer.getCustomerId() == 0) {
            customer.setCustomerId(super.nextId());
        }

        // [3] 保存到数据库
        customerMapper.upsertCustomer(customer);

        return Result.ok(customer);
    }

    /**
     * 从 Excel 中导入客户信息
     *
     * @param excel 客户信息的 Excel 文件
     */
    @Transactional(rollbackFor = Exception.class)
    public void importCustomers(File excel) {
        // 1. 导入得到客户对象
        // 2. 过滤掉无效的客户对象
        // 3. 保存客户到数据库
        ImportParams params = new ImportParams();
        params.setTitleRows(1);
        params.setHeadRows(1);

        // [1] 导入得到客户对象
        List<Customer> customers = ExcelImportUtil.importExcel(excel, Customer.class, params);

        // [2] 过滤掉无效的客户对象
        // [3] 保存客户到数据库
        customers.stream().filter(c -> StringUtils.isNotBlank(c.getCustomerSn())).forEach(c -> {
            c.setCustomerId(super.nextId());
            customerMapper.upsertCustomer(c);
        });
    }
}
