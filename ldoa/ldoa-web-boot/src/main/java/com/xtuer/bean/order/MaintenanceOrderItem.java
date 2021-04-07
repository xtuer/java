package com.xtuer.bean.order;

import lombok.Getter;
import lombok.Setter;

/**
 * 维保订单项
 */
@Getter
@Setter
public class MaintenanceOrderItem {
    /**
     * 维保订单 ID
     */
    private long maintenanceOrderId;

    /**
     * 维保订单项 ID
     */
    private long maintenanceOrderItemId;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 产品编码
     */
    private String productCode;

    /**
     * 规格型号
     */
    private String productModel;

    /**
     * 维修前电量
     */
    private String electricQuantityBefore;

    /**
     * 维修前软件版本
     */
    private String softwareVersionBefore;

    /**
     * 维修前硬件版本
     */
    private String hardwareVersionBefore;

    /**
     * 维修前功耗
     */
    private String powerDissipationBefore;

    /**
     * 维修后高温次数
     */
    private String temperatureBefore;

    /**
     * 芯片编号
     */
    private String chipCode;

    /**
     * 检测问题明细
     */
    private String checkDetails;

    /**
     * 维修明细
     */
    private String maintenanceDetails;

    /**
     * 探头换前编号
     */
    private String probeDetectorCodeBefore;

    /**
     * 维修后电量
     */
    private String electricQuantityAfter;

    /**
     * 维修后软件版本
     */
    private String softwareVersionAfter;

    /**
     * 维修后硬件版本
     */
    private String hardwareVersionAfter;

    /**
     * 维修后功耗
     */
    private String powerDissipationAfter;

    /**
     * 维修后高温次数
     */
    private String temperatureAfter;

    /**
     * 探头换后编号
     */
    private String probeDetectorCodeAfter;

    /**
     * 出厂时间
     */
    private String productionDate;
}
