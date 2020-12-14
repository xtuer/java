package com.xtuer.service;

import com.xtuer.bean.product.Product;
import com.xtuer.bean.product.ProductItem;
import com.xtuer.bean.Result;
import com.xtuer.bean.User;
import com.xtuer.mapper.ProductMapper;
import com.xtuer.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 产品服务
 */
@Service
public class ProductService extends BaseService {
    @Autowired
    private ProductMapper productMapper;

    /**
     * 创建或者更新产品
     *
     * @param product 产品
     * @param user    用户
     * @return payload 为更新后的产品
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<Product> upsertProduct(Product product, User user) {
        // 1. 如果 ID 无效，则分配一个 ID
        // 2. 如果产品编码被其他产品使用了，则失败
        // 3. 删除已有产品项
        // 4. 添加新的产品项
        // 5. 保存产品到数据库

        // [1] 如果 ID 无效，则分配一个 ID
        if (Utils.isInvalidId(product.getProductId())) {
            product.setProductId(super.nextId());
        }

        // [2] 如果产品编码被其他产品使用了，则失败
        if (!productMapper.isProductCodeAvailable(product.getProductId(), product.getCode())) {
            return Result.fail("产品编码不可用: " + product.getCode());
        }

        // [3] 删除已有产品项
        productMapper.deleteProductItems(product.getProductId());

        // [4] 添加新的产品项
        product.getItems().forEach(item -> {
            item.setProductId(product.getProductId()); // 确保产品 ID
        });
        if (product.getItems().size() > 0) {
            productMapper.insertProductItems(product.getItems());
        }

        // [5] 保存产品到数据库
        productMapper.upsertProduct(product);

        return Result.ok(product);
    }

    /**
     * 删除产品
     *
     * @param productId 产品 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteProduct(long productId) {
        productMapper.deleteProduct(productId);
        productMapper.deleteProductItems(productId); // 同时删除与产品项的关联关系
    }

    /**
     * 创建或者更新产品项
     *
     * @param item 产品项
     * @param user 用户
     * @return payload 为更新后的产品项
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<ProductItem> upsertProductItem(ProductItem item, User user) {
        // 1. 如果产品项 ID 无效，则分配一个
        // 2. 如果产品项编码被其他产品项使用了，则失败
        // 3. 保存产品项到数据库

        // [1] 如果产品项 ID 无效，则分配一个
        if (Utils.isInvalidId(item.getProductItemId())) {
            item.setProductItemId(super.nextId());
        }

        // [2] 如果产品项编码被其他产品项使用了，则失败
        if (!productMapper.isProductItemCodeAvailable(item.getProductItemId(), item.getCode())) {
            return Result.fail("物料编码不可用: " + item.getCode());
        }

        // [3] 保存产品项到数据库
        productMapper.upsertProductItem(item);

        return Result.ok(item);
    }
}
