package com.xtuer.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 订单
 */
@Getter
@Setter
@Accessors(chain = true)
public class Order {
    /**
     * 订单 ID
     */
    private long orderId;

    /**
     * 订单编号
     */
    private String orderSn;

    /**
     * 客户单位
     */
    private String customerCompany;

    /**
     * 客户联系人
     */
    private String customerContact;

    /**
     * 客户收件地址
     */
    private String customerAddress;

    /**
     * 下订单日期
     */
    private Date createDate;

    /**
     * 要求交货日期
     */
    private Date releaseDate;

    /**
     * 销售员
     */
    private String salesperson;

    /**
     * 是否校准
     */
    private boolean calibrated;

    /**
     * 校准信息
     */
    private String calibrationInfo;

    /**
     * 要求
     */
    private String requirement;

    /**
     * 附件 URL
     */
    private String attachment;

    /**
     * 订单创建日期
     */
    private Date createdAt;
}
