package com.xtuer.bean.product;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import java.util.LinkedList;
import java.util.List;

/**
 * 产品，每个产品有多个产品项 (物料)
 */
@Getter
@Setter
@Accessors(chain = true)
public class Product {
    /**
     * 产品 ID
     */
    private long productId;

    /**
     * 产品名称
     */
    @NotBlank(message="产品名称不能为空")
    private String name;

    /**
     * 产品编码
     */
    @NotBlank(message="产品编码不能为空")
    private String code;

    /**
     * 产品规格/型号
     */
    @NotBlank(message="产品规格/型号不能为空")
    private String model;

    /**
     * 产品描述
     */
    private String desc;

    /**
     * 创建产品的用户 ID
     */
    private long userId;

    /**
     * 产品项
     */
    private List<ProductItem> items = new LinkedList<>();
}
