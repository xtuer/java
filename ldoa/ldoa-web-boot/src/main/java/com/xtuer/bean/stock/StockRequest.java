package com.xtuer.bean.stock;

import com.xtuer.util.Utils;

import java.util.LinkedList;
import java.util.List;

/**
 * 出库操作申请，分 2 种出库情况:
 * A. 物料直接出库，每个物料一个出库记录
 * B. 订单出库 (记录有订单号)，需要查询到订单的产品，每个产品的物料，每个物料一个出库记录
 *
 * 出库申请会对应创建一个审批，其状态 state 通过关联审批的状态查询得到
 */
public class StockRequest {
    private static final String[] STATE_LABELS = { "初始化", "拒绝", "通过" };

    /**
     * 出库申请 ID
     */
    private long stockRequestId;

    /**
     * 库存操作类型: IN (入库), OUT (出库)
     */
    private StockRecord.Type type;

    /**
     * 订单号 [可选]
     */
    private String orderSn;

    /**
     * 审批状态: 0 (初始化), 1 (拒绝), 2 (通过)
     */
    private int state;

    /**
     * 申请者 ID
     */
    private long applicantId;

    /**
     * 申请者名字 (查询时从 User 中关联得到)
     */
    private String applicantNickname;

    /**
     * 描述，展示时需要使用
     */
    private String desc;

    /**
     * 库存操作记录，每个物料一个记录
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
