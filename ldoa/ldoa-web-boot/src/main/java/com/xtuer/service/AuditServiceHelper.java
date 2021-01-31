package com.xtuer.service;

import com.xtuer.bean.Result;
import com.xtuer.bean.User;
import com.xtuer.bean.audit.Audit;
import com.xtuer.bean.audit.AuditType;
import com.xtuer.bean.order.MaintenanceOrder;
import com.xtuer.bean.order.Order;
import com.xtuer.bean.stock.StockRequest;
import com.xtuer.exception.ApplicationException;
import com.xtuer.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 审批服务的辅助函数，用于处理指定类型的审批
 */
@Service
public class AuditServiceHelper {
    @Autowired
    private AuditService auditService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private StockService stockService;

    @Autowired
    private MaintenanceOrderService maintenanceOrderService;

    /**
     * 更新或者创建订单的审批
     *
     * @param applicant 申请人
     * @param order     订单
     * @return 返回操作结果
     * @exception ApplicationException 审批配置无效时抛异常
     */
    public Result<Audit> upsertOrderAudit(User applicant, Order order) {
        Objects.requireNonNull(order, "订单不能为空");
        String type = order.getType() == 0 ? "销售订单" : "样品订单";
        String desc = String.format("%s: %s, 客户: %s", type, order.getOrderSn(), order.getCustomerCompany());

        return auditService.upsertAudit(applicant, AuditType.ORDER, order.getOrderId(), Utils.toJson(order), desc, order.getCurrentAuditorId());
    }

    /**
     * 更新或者创建维保订单的审批
     *
     * @param applicant 申请人
     * @param order     维保订单
     * @return 返回操作结果
     * @exception ApplicationException 审批配置无效时抛异常
     */
    public Result<Audit> upsertMaintenanceOrderAudit(User applicant, MaintenanceOrder order) {
        Objects.requireNonNull(order, "维保订单不能为空");
        String desc = String.format("维保订单: %s, 客户: %s", order.getMaintenanceOrderSn(), order.getCustomerName());

        return auditService.upsertAudit(applicant, AuditType.MAINTENANCE_ORDER, order.getMaintenanceOrderId(), Utils.toJson(order), desc, 0);
    }

    /**
     * 创建物料出库申请
     *
     * @param applicant 申请人
     * @param request   出库申请
     * @return 返回操作结果
     * @exception ApplicationException 审批配置无效时抛异常
     */
    public Result<Audit> insertStockRequestAudit(User applicant, StockRequest request) {
        Objects.requireNonNull(request, "出库申请不能为空");
        String desc = String.format("出库单号: %s, 物料: %s", request.getStockRequestSn(), request.getDesc());

        return auditService.upsertAudit(applicant, AuditType.OUT_OF_STOCK, request.getStockRequestId(), Utils.toJson(request), desc, 0);
    }

    /**
     * target 的审批通过
     *
     * @param auditId  审批的 ID
     * @param targetId 审批目标的 ID
     * @param type     审批的类型
     */
    public void acceptTarget(long auditId, long targetId, AuditType type) {
        if (type == AuditType.ORDER) {
            orderService.acceptOrder(targetId);
        } else if (type == AuditType.MAINTENANCE_ORDER) {
            maintenanceOrderService.acceptOrder(targetId);
        } else if (type == AuditType.OUT_OF_STOCK) {
            stockService.acceptStockRequest(targetId);
        }
    }

    /**
     * target 的审批被拒绝
     *
     * @param auditId  审批的 ID
     * @param targetId 审批目标的 ID
     * @param type     审批的类型
     */
    public void rejectTarget(long auditId, long targetId, AuditType type) {
        if (type == AuditType.ORDER) {
            orderService.rejectOrder(targetId);
        } else if (type == AuditType.MAINTENANCE_ORDER) {
            maintenanceOrderService.rejectOrder(targetId);
        } else if (type == AuditType.OUT_OF_STOCK) {
            stockService.rejectStockRequest(targetId);
        }
    }
}
