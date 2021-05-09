package com.xtuer.bean.sales;

import com.xtuer.bean.order.Order;
import com.xtuer.util.Utils;
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
    public static final int STATE_INIT     = 0;
    public static final int STATE_WAIT_PAY = 1;
    public static final int STATE_PAID     = 2;
    public static final int STATE_COMPLETE = 3;

    /**
     * 状态值与对应的 Label: 数组的下标为状态值，对应的数组元素值为状态的 Label
     */
    private static final String[] STATE_LABELS = { "初始化", "待支付", "已支付", "完成" };

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
     * 总成交金额
     */
    private double dealAmount;

    /**
     * 净销售额
     */
    private double costDealAmount;

    /**
     * 订单咨询费
     */
    private double consultationFee;

    /**
     * 应收金额
     */
    private double shouldPayAmount;

    /**
     * 已收金额
     */
    private double paidAmount;

    /**
     * 收款时间
     */
    private Date paidAt;

    /**
     * 生产订单 ID
     */
    private long produceOrderId;

    /**
     * 生产订单
     */
    private Order produceOrder = new Order();

    /**
     * 状态: 0 (初始化), 1 (待支付), 2 (已支付), 3 (完成)
     */
    private int state;

    /**
     * 获取订单状态 Label
     *
     * @return 返回订单状态的 Label
     */
    public String getStateLabel() {
        return Utils.getStateLabel(STATE_LABELS, state);
    }
}
