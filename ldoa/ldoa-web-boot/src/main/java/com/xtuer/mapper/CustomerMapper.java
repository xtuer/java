package com.xtuer.mapper;

import com.xtuer.bean.Page;
import com.xtuer.bean.sales.Customer;
import com.xtuer.bean.sales.CustomerFilter;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 客户的 Mapper
 */
@Mapper
public interface CustomerMapper {
    /**
     * 查询符合条件的客户
     *
     * @return 返回客户的数组
     */
    List<Customer> findCustomers(CustomerFilter filter, Page page);

    /**
     * 插入或者更新客户
     *
     * @param customer 客户对象
     */
    void upsertCustomer(Customer customer);

    /**
     * 删除指定 ID 的客户
     *
     * @param customerId 客户 ID
     */
    void deleteCustomer(long customerId);
}
