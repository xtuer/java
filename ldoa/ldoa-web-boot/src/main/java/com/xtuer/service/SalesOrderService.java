package com.xtuer.service;

import com.xtuer.bean.Const;
import com.xtuer.bean.Page;
import com.xtuer.bean.Result;
import com.xtuer.bean.User;
import com.xtuer.bean.order.Order;
import com.xtuer.bean.order.OrderItem;
import com.xtuer.bean.sales.CustomerFinance;
import com.xtuer.bean.sales.SalesOrder;
import com.xtuer.bean.sales.SalesOrderFilter;
import com.xtuer.bean.sales.SalesOrderForPayment;
import com.xtuer.mapper.AuditMapper;
import com.xtuer.mapper.SalesOrderMapper;
import com.xtuer.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * 销售订单的服务
 */
@Service
@Slf4j
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
     * 查询指定 ID 的销售订单
     *
     * @param salesOrderId 销售订单 ID
     * @return 返回查询到的销售订单，查询不到返回 null
     */
    public SalesOrder findSalesOrder(long salesOrderId) {
        // 1. 查询销售订单
        // 2. 查询生产订单

        // [1] 查询销售订单
        SalesOrder salesOrder = salesOrderMapper.findSalesOrderById(salesOrderId);

        if (salesOrder == null) {
            return null;
        }

        // [2] 查询生产订单
        Order produceOrder = orderService.findOrder(salesOrder.getProduceOrderId());
        salesOrder.setProduceOrder(produceOrder);

        return salesOrder;
    }

    /**
     * 查询符合条件的销售订单
     *
     * @return 返回销售订单的数组
     */
    public List<SalesOrder> findSalesOrders(SalesOrderFilter filter, Page page) {
        final int searchType = filter.getSearchType();
        final Date current = new Date();

        // 待支付订单
        if (searchType == SalesOrderFilter.SEARCH_TYPE_SHOULD_PAY) {
            filter.setState(-1);
        }
        // 本月已收款订单
        else if (searchType == SalesOrderFilter.SEARCH_TYPE_PAID_THIS_CURRENT_MONTH) {
            filter.setPaidAtStart(Utils.monthStart(current));
            filter.setPaidAtEnd(Utils.monthEnd(current));
        }
        // 本年已收款订单
        else if (searchType == SalesOrderFilter.SEARCH_TYPE_PAID_THIS_CURRENT_YEAR) {
            filter.setPaidAtStart(Utils.yearStart(current));
            filter.setPaidAtEnd(Utils.yearEnd(current));
        }

        // 搜索所有订单或者应收款订单时设置签约时间，否则删除签约时间
        if (searchType == SalesOrderFilter.SEARCH_TYPE_ALL || searchType == SalesOrderFilter.SEARCH_TYPE_SHOULD_PAY) {
            filter.setAgreementStart(Utils.dayStart(filter.getAgreementStart()));
            filter.setAgreementEnd(Utils.dayEnd(filter.getAgreementEnd()));
        } else {
            filter.setAgreementStart(null);
            filter.setAgreementEnd(null);
        }

        return salesOrderMapper.findSalesOrders(filter, page);
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
        // 5. 计算销售订单的单价、咨询费等
        // 6. 保存销售订单到数据库

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

        // [5] 计算销售订单的咨询费、应收金额、净销售额
        double dealAmount = 0;
        double costDealAmount = 0;
        double orderConsultationFee = 0;
        for (OrderItem item : salesOrder.getProduceOrder().getItems()) {
            dealAmount += item.getPrice() * item.getCount() + item.getConsultationFee();
            costDealAmount += item.getCostPrice() * item.getCount();
            orderConsultationFee += item.getConsultationFee();
        }
        salesOrder.setDealAmount(dealAmount);
        salesOrder.setCostDealAmount(costDealAmount);
        salesOrder.setConsultationFee(orderConsultationFee);
        salesOrder.setState(SalesOrder.STATE_WAIT_PAY);

        // [6] 保存销售订单到数据库
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

    /**
     * 完成订单
     *
     * @param salesOrderId 销售订单 ID
     * @return 订单完成 result 的 success 为 true，否则为 false
     */
    public Result<Boolean> completeSalesOrder(long salesOrderId) {
        // 1. 查询订单
        // 2. 订单的已收金额大于等于应收金额则可以完成，否则提示错误

        // [1] 查询订单
        SalesOrder order = salesOrderMapper.findSalesOrderById(salesOrderId);

        if (order == null) {
            return Result.fail("订单不存在");
        }

        // [2] 订单的已收金额大于等于应收金额则可以完成，否则提示错误
        if (order.getPaidAmount() >= order.getShouldPayAmount()) {
            salesOrderMapper.completeSalesOrder(salesOrderId);
            log.info("[完成订单] 订单 [{}], 应收金额 [{}], 已收金额 [{}]", salesOrderId, order.getShouldPayAmount(), order.getPaidAmount());
            return Result.ok();
        } else {
            return Result.fail("已收金额大于等于应收金额才能完成订单");
        }
    }

    /**
     * 查询客户的财务信息: 累计订单金额、累计应收款、累计已收款
     *
     * @param customerId 客户 ID
     * @return 返回交易的财务信息
     */
    public CustomerFinance findCustomerFinance(long customerId) {
        // 1. 查询客户的财务信息
        // 2. 如果不存在，则创建默认的才需信息

        CustomerFinance finance = salesOrderMapper.findFinanceByCustomerId(customerId);

        // [2] 如果不存在，则创建默认的才需信息
        if (finance == null) {
            finance = new CustomerFinance();
            finance.setCustomerId(customerId);
        }

        return finance;
    }

    /**
     * 导出销售订单
     *
     * @param filter 过滤条件
     * @return 返回导出的 Excel 的 URL
     */
    public String exportSalesOrders(SalesOrderFilter filter) throws IOException {
        Page page = Page.of(1, Integer.MAX_VALUE);
        List<SalesOrder> orders = this.findSalesOrders(filter, page);
        return super.exportExcel("销售订单", SalesOrder.class, orders);
    }

    /**
     * 导出支付信息的销售订单
     *
     * @param filter 过滤条件
     * @return 返回导出的 Excel 的 URL
     */
    public String exportSalesOrdersForPayment(SalesOrderFilter filter) throws IOException {
        Page page = Page.of(1, Integer.MAX_VALUE);
        List<SalesOrder> orders = this.findSalesOrders(filter, page);
        List<SalesOrderForPayment> ps = new LinkedList<>();

        for (SalesOrder order : orders) {
            SalesOrderForPayment p = new SalesOrderForPayment();
            BeanUtils.copyProperties(order, p);
            ps.add(p);
        }

        // 导出文件名
        String name = "";
        switch (filter.getSearchType()) {
            case SalesOrderFilter.SEARCH_TYPE_ALL:
                name = "收款订单-所有订单";
                break;
            case SalesOrderFilter.SEARCH_TYPE_SHOULD_PAY:
                name = "收款订单-应收款订单";
                break;
            case SalesOrderFilter.SEARCH_TYPE_PAID_THIS_CURRENT_MONTH:
                name = "收款订单-本月已收款";
                break;
            case SalesOrderFilter.SEARCH_TYPE_PAID_THIS_CURRENT_YEAR:
                name = "收款订单-本年已收款";
                break;
        }

        return super.exportExcel(name, SalesOrderForPayment.class, ps);
    }
}
