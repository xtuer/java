package com.xtuer.mapper;

import com.xtuer.bean.Customer;
import org.apache.ibatis.annotations.Mapper;

/**
 * 客户的 Mapper
 */
@Mapper
public interface CustomerMapper {
    /**
     * 插入或者更新客户
     *
     * @param customer 客户对象
     */
    void upsertCustomer(Customer customer);
}
