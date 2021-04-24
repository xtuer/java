package com.xtuer.service;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.xtuer.bean.Customer;
import com.xtuer.mapper.CustomerMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 客户的服务
 */
@Service
public class CustomerService extends BaseService {
    @Autowired
    private CustomerMapper customerMapper;

    /**
     * 从 Excel 中导入客户信息
     *
     * @param excelPath 客户信息的 Excel 文件路径
     */
    @Transactional(rollbackFor = Exception.class)
    public void importCustomers(String excelPath) {
        // 1. 导入得到客户对象
        // 2. 过滤掉无效的客户对象
        // 3. 保存客户到数据库
        ImportParams params = new ImportParams();
        params.setTitleRows(1);
        params.setHeadRows(1);

        // [1] 导入得到客户对象
        List<Customer> customers = ExcelImportUtil.importExcel(new File(excelPath), Customer.class, params);

        // [2] 过滤掉无效的客户对象
        // [3] 保存客户到数据库
        customers.stream().filter(c -> StringUtils.isNotBlank(c.getCustomerSn())).forEach(c -> {
            c.setCustomerId(super.nextId());
            customerMapper.upsertCustomer(c);
        });
    }
}
