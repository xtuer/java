package com.xtuer.bean.sales;

import cn.afterturn.easypoi.excel.annotation.Excel;
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
    @Excel(name = "订单编号", width = 24, orderNum = "1")
    private String salesOrderSn;

    /**
     * 主题
     */
    @NotNull(message = "主题不能为空")
    @Excel(name = "客户", width = 20, orderNum = "3")
    private String topic;

    /**
     * 签约日期
     */
    @NotNull(message = "签约日期不能为空")
    @Excel(name = "签约日期", width = 20, orderNum = "8", exportFormat = "yyyy-MM-dd")
    private Date agreementDate;

    /**
     * 交货日期
     */
    @Excel(name = "交货日期", width = 20, orderNum = "9", exportFormat = "yyyy-MM-dd")
    @NotNull(message = "交货日期不能为空")
    private Date deliveryDate;

    /**
     * 负责人
     */
    @Excel(name = "负责人", width = 20, orderNum = "4")
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
    @Excel(name = "客户", width = 40, orderNum = "2")
    private String customerName;

    /**
     * 联系人
     */
    @NotNull(message = "联系人不能为空")
    @Excel(name = "联系人", width = 20, orderNum = "7")
    private String customerContact;

    /**
     * 行业
     */
    @Excel(name = "行业", width = 20, orderNum = "5")
    private String business;

    /**
     * 执行单位
     */
    @Excel(name = "执行单位", width = 20, orderNum = "6")
    private String workUnit;

    /**
     * 备注
     */
    @Excel(name = "备注", width = 20, orderNum = "14")
    private String remark;

    /**
     * 净销售额
     */
    @Excel(name = "净销售额", width = 20, orderNum = "10")
    private double costDealAmount;

    /**
     * 订单咨询费
     */
    @Excel(name = "咨询费", width = 20, orderNum = "11")
    private double consultationFee;

    /**
     * 总成交金额
     */
    @Excel(name = "总成交金额", width = 20, orderNum = "12")
    private double dealAmount;

    /**
     * 应收金额
     */
    @Excel(name = "应收金额", width = 20, orderNum = "13")
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
