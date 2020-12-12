package com.xtuer.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductMapper {
    /**
     * 使用产品 ID 作为条件，锁住产品
     *
     * @param id 产品 ID
     */
    void lockProduct(int id);

    /**
     * 查询指定 ID 产品的数量
     *
     * @param id 产品 ID
     * @return 返回产品的数量
     */
    int findProductCount(int id);

    /**
     * 更新产品的数量
     *
     * @param id    产品 ID
     * @param count 数量
     */
    void updateProductCount(int id, int count);
}
