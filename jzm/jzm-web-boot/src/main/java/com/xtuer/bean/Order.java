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
    private long   id;               // 订单 ID
    private String customerName;     // 客户名称
    private String type;             // 订单类型: 订货、样品
    private String brand;            // 品牌: P+H、BD、其他
    private String softwareVersion;  // 软件版本
    private String personInCharge;   // 负责人
    private int    status;           // 订单状态: 0 (等待备件)、1 (组装中)、2 (完成组装)
    private Date orderDate;          // 下单日期
    private Date startAssembleDate;  // 开始组装日期
    private Date finishAssembleDate; // 完成组装日期

    private List<OrderItem> orderItems;
}
