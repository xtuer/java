package com.xtuer.bean.stock;

import com.xtuer.util.Utils;

import java.util.LinkedList;
import java.util.List;

/**
 * 出库操作申请
 */
public class StockRequest {
    private static final String[] STATE_LABELS = { "初始化", "审批中", "审批拒绝", "审批通过", "完成" };

    /**
     * 出库申请 ID
     */
    private long stockRequestId;

    /**
     * 库存操作类型: IN (入库), OUT (出库)
     */
    private StockRecord.Type type;

    /**
     * 订单号 (可选)
     */
    private String orderSn;

    /**
     * 状态: 0 (初始化), 1 (待审批), 2 (审批拒绝), 3 (审批完成), 4 (完成)
     */
    private int state;

    /**
     * 申请者 ID
     */
    private long applicantId;

    /**
     * 入库项目
     */
    private List<StockRecord> items = new LinkedList<>();

    /**
     * 获取订单状态 Label
     *
     * @return 返回订单状态的 Label
     */
    public String getStatusLabel() {
        return Utils.getStateLabel(STATE_LABELS, state);
    }
}
