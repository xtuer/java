package com.xtuer.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * 订单
 */
@Getter
@Setter
@Accessors(chain = true)
public class Order {
    private static final String[] STATUS_LABELS = { "未知", "已完成", "进行中", "挂起中" };

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
     * 下订日期
     */
    private Date orderDate;

    /**
     * 交货日期
     */
    private Date deliveryDate;

    /**
     * 销售员 ID
     */
    private long salespersonId;

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

    /**
     * 状态
     */
    private int status;

    /**
     * 订单的产品编码，使用逗号分隔，方便搜索
     */
    private String productCodes;

    /**
     * 订单项
     */
    private List<OrderItem> items = new LinkedList<>();

    /**
     * 销售员
     */
    private User salesperson = new User();

    /**
     * 获取订单状态 Label
     *
     * @return 返回订单状态的 Label
     */
    public String getStatusLabel() {
        if (status >= 0 && status < STATUS_LABELS.length) {
            return STATUS_LABELS[status];
        } else {
            return "未知";
        }
    }
}
