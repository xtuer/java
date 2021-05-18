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
     * 查询指定 ID 的客户
     *
     * @param customerId 客户 ID
     * @return 返回查询到的客户，查询不到返回 null
     */
    Customer findCustomerById(long customerId);

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
     * @param forImport 是否导入，导入时不更新 contacts_json 字段
     */
    void upsertCustomer(Customer customer, boolean forImport);

    /**
     * 删除指定 ID 的客户
     *
     * @param customerId 客户 ID
     */
    void deleteCustomer(long customerId);

    /**
     * 判断客户编号是否被使用过
     *
     * @param customerId 客户 ID
     * @param customerSn 客户编号
     * @return 被使用过返回 true，否则返回 false
     */
    boolean isCustomerSnUsed(long customerId, String customerSn);
}
