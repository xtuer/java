package com.xtuer.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 出库入库日志
 */
@Getter
@Setter
@Accessors(chain = true)
public class SpareWarehousingLog {
    private String username       ; // 用户账号
    private String type           ; // 操作类型: 出库、入库
    private long   spareId        ; // 备件 ID
    private String spareSn        ; // 备件入库单号
    private int    oldChipQuantity; // 操作前芯片数量
    private int    newChipQuantity; // 操作后芯片数量
    private int    quantity       ; // 出入库的数量
    private Date   date           ; // 操作时间
    private String desc           ; // 说明

    public String getType() {
        return quantity > 0 ? "入库" : "出库";
    }
}
