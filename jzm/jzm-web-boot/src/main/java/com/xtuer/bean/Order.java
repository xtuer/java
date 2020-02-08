package com.xtuer.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * 订单
 */
@Getter
@Setter
@Accessors(chain = true)
public class Order {
    private long   id;              // 订单 ID
    private String customerName;    // 客户名称
    private String process;         // 生产进程: 等待备件、组装中
    private String type;            // 订单类型: 订货、样品
    private String brand;           // 品牌: P+H、BD、其他
    private String softwareVersion; // 软件版本
    private String personInCharge;  // 负责人
    private Date   orderDate;       // 订单日期
    private int    status;          // 订单状态

    private List<OrderItem> orderItems;
}
