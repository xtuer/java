package com.xtuer.bean.stock;

import com.xtuer.util.Utils;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

/**
 * 出库操作申请，分 2 种出库情况:
 * A. 物料直接出库，每个物料一个出库记录
 * B. 订单出库 (记录有订单号)，需要查询到订单的产品，每个产品的物料，每个物料一个出库记录
 *
 * 出库申请会对应创建一个审批
 */
@Getter
@Setter
public class StockRequest {
    private static final String[] STATE_LABELS = { "初始化", "审批中", "审批拒绝", "审批完成", "完成" };

    /**
     * 出库申请 ID
     */
    private long stockRequestId;

    /**
     * 库存操作类型: IN (入库), OUT (出库)
     */
    private StockRecord.Type type;

    /**
     * 订单 ID [可选]
     */
    private long orderId;

    /**
     * 状态: 0 (初始化), 1 (待审批), 2 (审批拒绝), 3 (审批完成), 4 (完成)
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
    public String getStateLabel() {
        return Utils.getStateLabel(STATE_LABELS, state);
    }
}
