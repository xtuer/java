package com.xtuer.service;

import com.xtuer.bean.Const;
import com.xtuer.bean.Result;
import com.xtuer.bean.User;
import com.xtuer.bean.order.Order;
import com.xtuer.bean.sales.SalesOrder;
import com.xtuer.mapper.AuditMapper;
import com.xtuer.mapper.SalesOrderMapper;
import com.xtuer.util.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 销售订单的服务
 */
@Service
public class SalesOrderService extends BaseService {
    @Autowired
    private OrderService orderService;

    @Autowired
    private AuditService auditService;

    @Autowired
    private AuditMapper auditMapper;

    @Autowired
    private SalesOrderMapper salesOrderMapper;

    /**
     * 查询符合条件的销售订单
     *
     * @return 返回销售订单的数组
     */
    public List<SalesOrder> findSalesOrders() {
        return salesOrderMapper.findSalesOrders();
    }

    /**
     * 更新或者插入销售订单
     *
     * @param salesOrder 销售订单
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<SalesOrder> upsertSalesOrder(SalesOrder salesOrder, User salesperson) {
        // 1. 数据校验
        // 2. 生成 ID 和编号，如果不存在
        // 3. 生成生产订单
        // 4. 自动通过生产订单的审批
        // 5. 保存销售订单到数据库

        // [2] 生成 ID 和编号，如果不存在
        if (!Utils.isValidId(salesOrder.getSalesOrderId())) {
            salesOrder.setSalesOrderId(super.nextId());
            salesOrder.setSalesOrderSn(this.nextSalesOrderSn());
        }

        // [3] 生成生产订单
        Order produceOrder = salesOrder.getProduceOrder();
        produceOrder.setCustomerCompany(salesOrder.getWorkUnit());        // 客户单位
        produceOrder.setCustomerContact(salesOrder.getCustomerContact()); // 客户联系人
        produceOrder.setCustomerAddress("---");                           // 客户收件地址
        produceOrder.setType(0);                                          // 订单类型: 生产订单
        produceOrder.setOrderDate(salesOrder.getAgreementDate());         // 订单日期
        produceOrder.setDeliveryDate(salesOrder.getDeliveryDate());       // 要求交货日期
        produceOrder.setCurrentAuditorId(Const.DEFAULT_PASS_AUDITOR_ID);  // 审批员的 ID
        produceOrder.setRequirement(salesOrder.getRemark());              // 备注

        Result<Order> ret = orderService.upsertOrder(produceOrder, salesperson);
        if (!ret.isSuccess()) {
            return Result.fail(ret.getMessage());
        }

        // 保存更新后同步生产订单到销售订单
        salesOrder.setProduceOrder(ret.getData());
        salesOrder.setProduceOrderId(produceOrder.getOrderId());

        // [4] 自动通过生产订单的审批
        auditService.autoPassAudit(salesOrder.getProduceOrderId(), "销售订单生成的生产订单，自动审批通过");

        // [5] 保存销售订单到数据库
        salesOrderMapper.upsertSalesOrder(salesOrder);

        return Result.ok(salesOrder);
    }

    /**
     * 生成销售订单号，格式为 XC-20200806-0001
     *
     * @return 返回订单号
     */
    public String nextSalesOrderSn() {
        // XC 不动, 20200806 为年月日，根据日期自动生成, 0001 为流水号 (0001,0002,003……)，每年再从 0001 开始
        return super.nextSnByYear(Const.SN_PREFIX_SALES_ORDER);
    }
}
