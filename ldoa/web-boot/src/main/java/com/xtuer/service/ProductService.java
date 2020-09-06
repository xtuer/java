package com.xtuer.service;

import com.xtuer.bean.Product;
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
     * @return 返回更新后的产品
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
}
