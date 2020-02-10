package com.xtuer.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 备件
 */
@Getter
@Setter
@Accessors(chain = true)
public class Spare {
    private long   id                  ; // 备件 ID
    private String sn                  ; // 入库单号
    private String type                ; // 备件类型
    private String chipSn              ; // 芯片编号
    private String chipProductionDate  ; // 芯片生产时间
    private String chipAgingDate       ; // 芯片老化时间
    private String chipPowerConsumption; // 芯片功耗
    private String firmwareVersion     ; // 固件版本
    private String softwareVersion     ; // 软件版本
    private int    chipQuantity        ; // 芯片数量
}
