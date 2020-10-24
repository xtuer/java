package com.xtuer.bean;

import com.xtuer.util.Utils;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
    private static final String[] STATUS_LABELS = { "初始化", "审批中", "审批拒绝", "审批完成", "完成" };

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
    @NotBlank(message = "客户单位不能为空")
    private String customerCompany;

    /**
     * 客户联系人
     */
    @NotBlank(message = "客户联系人不能为空")
    private String customerContact;

    /**
     * 客户收件地址
     */
    @NotBlank(message = "客户收件地址不能为空")
    private String customerAddress;

    /**
     * 订单日期
     */
    @NotNull(message = "订单日期不能为空")
    private Date orderDate;

    /**
     * 交货日期
     */
    @NotNull(message = "交货日期不能为空")
    private Date deliveryDate;

    /**
     * 销售员
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
     * 附件 ID
     */
    private long attachmentId;

    /**
     * 订单创建日期
     */
    private Date createdAt;

    /**
     * 状态: 0 (初始化), 1 (待审批), 2 (审批拒绝), 3 (审批完成), 4 (完成)
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
     * 附件
     */
    private UploadedFile attachment = new UploadedFile();

    /**
     * 获取订单状态 Label
     *
     * @return 返回订单状态的 Label
     */
    public String getStatusLabel() {
        return Utils.getStatusLabel(STATUS_LABELS, status);
    }
}
