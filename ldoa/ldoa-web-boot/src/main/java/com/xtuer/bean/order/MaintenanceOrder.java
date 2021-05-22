package com.xtuer.bean.order;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.xtuer.util.Utils;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * 维保订单 (维修保养订单)
 */
@Getter
@Setter
public class MaintenanceOrder {
    public static final int STATE_INIT     = 0;
    public static final int STATE_AUDITING = 1;
    public static final int STATE_REJECTED = 2;
    public static final int STATE_ACCEPTED = 3;
    public static final int STATE_COMPLETE = 4;

    /**
     * 状态值与对应的 Label: 数组的下标为状态值，对应的数组元素值为状态的 Label
     */
    private static final String[] STATE_LABELS = { "初始化", "审批中", "审批拒绝", "审批通过", "完成" };

    /**
     * 维保订单 ID
     */
    private long maintenanceOrderId;

    /**
     * 维保订单 SN
     */
    @Excel(name = "维保单号", width = 24, orderNum = "1")
    private String maintenanceOrderSn;

    /**
     * 售后服务人员 ID
     */
    private long servicePersonId;

    /**
     * 售后服务人员名字
     */
    @Excel(name = "售后服务人员", width = 20, orderNum = "10")
    private String servicePersonName;

    /**
     * 客户名字
     */
    @Excel(name = "客户", width = 40, orderNum = "2")
    private String customerName;

    /**
     * 保养
     */
    private boolean maintainable;

    /**
     * 维修
     */
    private boolean repairable;

    /**
     * 销售人员名字
     */
    @Excel(name = "销售人员", width = 20, orderNum = "11")
    private String salespersonName;

    /**
     * 收货日期
     */
    @Excel(name = "收货日期", width = 20, orderNum = "12", exportFormat = "yyyy-MM-dd")
    private Date receivedDate;

    /**
     * 创建日期
     */
    private Date createdAt;

    /**
     * 产品编码
     */
    @Excel(name = "产品编码", width = 20, orderNum = "4")
    private String productCode;

    /**
     * 产品名称
     */
    @Excel(name = "产品名称", width = 20, orderNum = "3")
    private String productName;

    /**
     * 规格/型号
     */
    @Excel(name = "规格/型号", width = 20, orderNum = "5")
    private String productModel;

    /**
     * 产品数量
     */
    @Excel(name = "产品数量", width = 20, orderNum = "6")
    private int productCount;

    /**
     * 物料名称
     */
    private String productItemName;

    /**
     * 批次
     */
    @Excel(name = "", width = 20)
    private String productItemBatch;

    /**
     * 物料数量
     */
    private int productItemCount;

    /**
     * 配件
     */
    private String accessories;

    /**
     * 是否需要证书
     */
    private boolean needCertificate;

    /**
     * 客户反馈的问题
     */
    @Excel(name = "反馈的问题", width = 40, orderNum = "8")
    private String problem;

    /**
     * 进度
     */
    @Excel(name = "处理进度", width = 20, orderNum = "9")
    private String progress;

    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 状态: 0 (初始化), 1 (待审批), 2 (审批拒绝), 3 (审批完成), 4 (完成)
     */
    @Excel(name = "状态", width = 20, orderNum = "7", replace = { "初始化_0", "待审批_1", "审批拒绝_2", "审批完成_3", "完成_4" })
    private int state;

    /**
     * 当前审批员 ID
     */
    private long currentAuditorId;

    /**
     * 维保订单项
     */
    private List<MaintenanceOrderItem> items = new LinkedList<>();

    /**
     * 是否提交，为 false 表示暂存，不创建审批
     */
    private boolean committed;

    /**
     * 获取订单状态 Label
     *
     * @return 返回订单状态的 Label
     */
    public String getStateLabel() {
        return Utils.getStateLabel(STATE_LABELS, state);
    }
}
