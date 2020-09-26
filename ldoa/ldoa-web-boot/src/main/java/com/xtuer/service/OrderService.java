package com.xtuer.service;

import com.xtuer.bean.Order;
import com.xtuer.mapper.OrderMapper;
import com.xtuer.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 订单服务
 */
@Service
public class OrderService extends BaseService {
    @Autowired
    private CommonService commonService;

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 插入或者更新订单
     *
     * @param order 订单
     */
    @Transactional(rollbackFor = Exception.class)
    public void upsertOrder(Order order) {
        // 1. 如果订单 ID 无效，则说明是创建，分配订单 ID 和订单号
        // 2. 删除订单的订单项
        // 3. 重新创建订单项
        // 4. 保存附件
        // 5. 保存订单到数据库

        // [1] 如果订单 ID 无效，则说明是创建，分配订单 ID 和订单号
        if (Utils.isInvalidId(order.getOrderId())) {
            order.setOrderId(super.nextId());
            order.setOrderSn(nextOrderSn());
        }

        // [2] 删除订单的订单项
        orderMapper.deleteOrderItems(order.getOrderId());

        // [3] 重新创建订单项
        if (!order.getItems().isEmpty()) {
            orderMapper.insertOrderItems(order.getItems());
        }

        // [4] 保存附件
        order.setAttachment(repoFileService.moveTempFileToRepo(order.getAttachment()));

        // [5] 保存订单到数据库
        orderMapper.upsertOrder(order);
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
