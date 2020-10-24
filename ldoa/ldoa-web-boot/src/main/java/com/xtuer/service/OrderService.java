package com.xtuer.service;

import com.xtuer.bean.*;
import com.xtuer.bean.audit.Audit;
import com.xtuer.bean.audit.AuditType;
import com.xtuer.mapper.FileMapper;
import com.xtuer.mapper.OrderMapper;
import com.xtuer.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 订单服务
 */
@Service
public class OrderService extends BaseService {
    @Autowired
    private CommonService commonService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private AuditService auditService;

    @Autowired
    private FileMapper fileMapper;

    /**
     * 查询指定 ID 的订单
     *
     * @param orderId 订单 ID
     * @return 返回查询到的订单
     */
    public Order findOrder(long orderId) {
        // 1. 查询订单的基础数据 (包含了订单项，销售员等)
        // 2. 去掉 orderItemId 为 0 的 item (没有订单项时 MyBatis 的绑定造成多出一个无效的订单项)
        // 3. 查询订单的附件
        Order order = orderMapper.findOrderById(orderId);

        if (order == null) {
            return null;
        }

        // [2] 去掉 orderItemId 为 0 的 item (没有订单项时 MyBatis 的绑定造成多出一个无效的订单项)
        order.setItems(order.getItems().stream().filter(item -> item.getOrderItemId() > 0).collect(Collectors.toList()));

        // [3] 查询订单的附件
        UploadedFile attachment = fileMapper.findUploadedFileById(order.getAttachmentId());
        if (attachment != null) {
            order.setAttachment(attachment);
        }

        return order;
    }

    /**
     * 插入或者更新订单
     *
     * @param order 订单
     * @param salesperson 销售员
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<Order> upsertOrder(Order order, User salesperson) {
        // 1. 如果订单 ID 无效，则说明是创建，分配订单 ID、订单号和销售员
        // 2. 设置订单的产品编码
        // 3. 设置订单项
        // 4. 创建订单的审批，失败则不继续处理
        // 5. 删除订单的订单项
        // 6. 重新创建订单项
        // 7. 保存附件
        // 8. 保存订单到数据库

        // [1] 如果订单 ID 无效，则说明是创建，分配订单 ID 和订单号
        if (Utils.isInvalidId(order.getOrderId())) {
            order.setOrderId(super.nextId());
            order.setOrderSn(nextOrderSn());
            order.setSalespersonId(salesperson.getUserId());
            order.setStatus(1); // 状态为等待审批
        }

        // [2] 设置订单的产品编码
        order.setProductCodes(this.getOrderProductCodes(order));

        // [3] 设置订单项
        for (OrderItem item : order.getItems()) {
            item.setOrderId(order.getOrderId());

            if (item.getProduct() != null) {
                item.setProductId(item.getProduct().getProductId());
            }

            if (Utils.isInvalidId(item.getOrderItemId())) {
                item.setOrderItemId(super.nextId());
            }
        }

        // [4] 创建订单的审批，失败则不继续处理
        Result<String> result = auditService.upsertOrderAudit(salesperson, order);
        if (!result.isSuccess()) {
            return Result.fail(result);
        }

        // [5] 删除订单的订单项
        orderMapper.deleteOrderItems(order.getOrderId());

        // [6] 重新创建订单项
        if (!order.getItems().isEmpty()) {
            orderMapper.insertOrderItems(order.getItems());
        }

        // [7] 保存附件
        repoFileService.moveTempFileToRepo(order.getAttachmentId());

        // [8] 保存订单到数据库
        orderMapper.upsertOrder(order);

        return Result.ok(order);
    }

    /**
     * 获取订单的所有产品编码
     *
     * @param order 订单
     * @return 返回产品编码
     */
    public String getOrderProductCodes(Order order) {
        return order.getItems()                    // 获取订单项
                .stream()                          // 转为 stream
                .map(OrderItem::getProduct)        // 获取订单项的产品
                .filter(Objects::nonNull)          // 去掉 null 的产品
                .map(Product::getCode)             // 获取产品的编码
                .collect(Collectors.joining(",")); // 返回逗号分隔的产品编码
    }

    /**
     * 生成订单号，格式为 XSDD-20200806-0001
     *
     * @return 返回订单号
     */
    public String nextOrderSn() {
        // XSDD 不动, 20200806 为年月日，根据日期自动生成, 0001 为流水号 (0001,0002,003……)，每年再从 0001 开始
        String year = DateTimeFormatter.ofPattern("yyyy").format(LocalDate.now());
        String date = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now());
        String name = "ORDER-" + year;
        String sn   = commonService.nextSequence(name) + "";
        String orderSn = "XSDD-" + date + "-" + StringUtils.leftPad(sn, 4, "0");

        return orderSn;
    }
}
