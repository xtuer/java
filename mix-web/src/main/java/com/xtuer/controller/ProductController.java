package com.xtuer.controller;

import com.xtuer.bean.Result;
import com.xtuer.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {
    @Autowired
    private ProductService productService;

    /**
     * 增加产品的数量
     *
     * 网址: http://localhost:8080/api/products/{productId}/increase?inc=10&mode=1
     * 参数:
     *      inc : 增加的数量
     *      mode: 执行模式: 1 (无锁)、2 (DB 锁)、3 (Zookeeper 分布式锁)、4 (注解实现的分布式锁)
     *
     * @param productId 产品 ID
     * @param inc       增加的数量
     * @param mode      执行模式: 1 (无锁)、2 (DB 锁)、3 (Zookeeper 分布式锁)
     */
    @GetMapping("/api/products/{productId}/increase")
    public Result<String> increaseProductCount(@PathVariable int productId,
                                               @RequestParam int inc,
                                               @RequestParam int mode) throws Exception {
        long elapsed = productService.increaseProductCount(productId, inc, mode);
        return Result.ok("执行时间: " + elapsed + " 毫秒");
    }
}
