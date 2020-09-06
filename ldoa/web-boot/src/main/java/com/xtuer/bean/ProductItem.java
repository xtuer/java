package com.xtuer.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * 产品项，又叫物料
 */
@Getter
@Setter
@Accessors(chain = true)
public class ProductItem {
    /**
     * 产品项 ID
     */
    private long productItemId;

    /**
     * 所属产品 ID
     */
    private long productId;

    /**
     * 物料名称
     */
    @NotBlank(message="物料名称不能为空")
    private String name;

    /**
     * 物料编码
     */
    @NotBlank(message="物料编码不能为空")
    private String code;

    /**
     * 物料类型
     */
    @NotBlank(message="物料类型不能为空")
    private String type;

    /**
     * 物料描述
     */
    private String desc;

    /**
     * 物料规格/型号
     */
    @NotBlank(message="物料规格/型号不能为空")
    private String model;

    /**
     * 标准/规范
     */
    @NotBlank(message="标准/规范不能为空")
    private String standard;

    /**
     * 材质
     */
    @NotBlank(message="材质不能为空")
    private String material;

    /**
     * 数量
     */
    private int count;
}
