package com.xtuer.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 订单项
 */
@Getter
@Setter
@Accessors(chain = true)
public class OrderItem {
    private long   orderId;    // 订单 ID
    private long   id;         // 订单项 ID
    private String type;       // 型号
    private String sn;         // 序列号
    private String chipSn;     // 芯片编号
    private String shellColor; // 外壳颜色
    private String shellBatch; // 外壳批次
    private String sensorInfo; // 传感器信息
    private String circleInfo; // Ο 型圈信息
    private int     status;    // 订单项的状态
    private int     count;     // 数量
    private boolean deleted;   // 被删除的标记
}
