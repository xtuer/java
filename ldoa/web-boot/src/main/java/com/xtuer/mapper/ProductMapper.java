package com.xtuer.mapper;

import com.xtuer.bean.Page;
import com.xtuer.bean.Product;
import com.xtuer.bean.ProductItem;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 产品的 Mapper
 */
@Mapper
public interface ProductMapper {
    /**
     * 查询指定 ID 的产品
     *
     * @param productId 产品 ID
     * @return 返回查询到的产品，查询不到时返回 null
     */
    Product findProductById(long productId);

    /**
     * 查询符合条件的产品
     *
     * @param filter 过滤条件
     * @param page   分页对象
     * @return 返回查询到的产品数组
     */
    List<Product> findProducts(Product filter, Page page);

    /**
     * 查询指定 ID 的产品项
     *
     * @param productItemId 产品项 ID
     * @return 返回查询到的产品项，查询不到时返回 null
     */
    ProductItem findProductItemById(long productItemId);

    /**
     * 检测产品编码是否可用 (没有被其他产品使用即为可用)
     *
     * @param productId 产品 ID
     * @param code      产品编码
     * @return 产品编码可用返回 true，否则返回 false
     */
    boolean isProductCodeAvailable(long productId, String code);

    /**
     * 创建或者更新产品
     *
     * @param product 产品
     */
    void upsertProduct(Product product);

    /**
     * 删除产品的产品项
     *
     * @param productId 产品 ID
     */
    void deleteProductItems(long productId);

    /**
     * 添加产品项
     *
     * @param items 产品项数组
     */
    void insertProductItems(List<ProductItem> items);
}
