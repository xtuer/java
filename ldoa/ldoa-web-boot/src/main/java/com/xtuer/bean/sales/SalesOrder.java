package com.xtuer.bean.sales;

import com.xtuer.bean.order.Order;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 销售订单
 */
@Getter
@Setter
public class SalesOrder {
    /**
     * 订单 ID
     */
    private long salesOrderId;

    /**
     * 订单编号
     */
    private String salesOrderSn;

    /**
     * 主题
     */
    @NotNull(message = "主题不能为空")
    private String topic;

    /**
     * 签约日期
     */
    @NotNull(message = "签约日期不能为空")
    private Date agreementDate;

    /**
     * 交货日期
     */
    @NotNull(message = "交货日期不能为空")
    private Date deliveryDate;

    /**
     * 负责人
     */
    private String ownerName;

    /**
     * 负责人 ID
     */
    @Min(value = 1, message = "请选择负责人")
    private long ownerId;

    /**
     * 客户 ID
     */
    @Min(value = 1, message = "请选择客户")
    private long customerId;

    /**
     * 客户
     */
    private String customerName;

    /**
     * 联系人
     */
    @NotNull(message = "联系人不能为空")
    private String customerContact;

    /**
     * 行业
     */
    private String business;

    /**
     * 执行单位
     */
    private String workUnit;

    /**
     * 备注
     */
    private String remark;

    /**
     * 生产订单 ID
     */
    private long produceOrderId;

    /**
     * 生产订单
     */
    private Order produceOrder = new Order();
}
