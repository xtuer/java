package com.xtuer.controller;

import com.xtuer.bean.Product;
import com.xtuer.bean.Result;
import com.xtuer.bean.Urls;
import com.xtuer.bean.User;
import com.xtuer.service.ProductService;
import com.xtuer.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 产品控制器
 */
@RestController
public class ProductController extends BaseController {
    @Autowired
    private ProductService productService;

    /**
     * 创建或者更新产品
     *
     * 网址: http://localhost:8080/api/products/{productId}
     * 参数: 无
     * 请求体: 产品的 JSON 字符串
     *     productId: 产品 ID
     *     name: 产品名称
     *     code: 产品编码
     *     desc: 产品描述
     *     model: 产品规格/型号
     *     items: [ { productItemId, count } ] 产品项的数组
     *
     * @param product 产品
     * @return payload 为更新后的产品
     */
    @PutMapping(Urls.API_PRODUCTS_BY_ID)
    public Result<Product> upsertProduct(@RequestBody @Valid Product product, BindingResult bindingResult) {
        // 如有参数错误，则返回错误信息给客户端
        if (bindingResult.hasErrors()) {
            return Result.fail(Utils.getBindingMessage(bindingResult));
        }

        User user = super.getCurrentUser();
        return productService.upsertProduct(product, user);
    }
}
