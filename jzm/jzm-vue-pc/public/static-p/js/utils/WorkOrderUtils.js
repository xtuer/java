/**
 * 工单
 */
export default class WorkOrderUtils {
    /**
     * 创建新工单
     */
    static newWorkOrder() {
        return {
            id             : Utils.nextId(),
            customerName   : '', // 客户名称
            softwareVersion: '', // 软件版本
            personInCharge : '', // 负责人
            status         : 0,          // 状态: 0 (等待备件)、1 (组装中)、2 (完成组装)
            orderDate      : new Date(), // 订单日期
            orderItems     : [],         // 维修项
            neu            : true,       // 新创建的订单为 true，否则为 false
        };
    }

    /**
     * 克隆工单
     */
    static cloneWorkOrder(workOrder) {
        let clone  = JSON.parse(JSON.stringify(workOrder));
        clone.date = workOrder.date; // 时间对象单独处理

        return clone;
    }

    /**
     * 创建新的工单项
     */
    static newWorkOrderItem() {
        return {
            id             : Utils.nextId(),
            brand          : '', // 品牌: P+H/BD/EBRO
            type           : '', // 型号
            sn             : '', // 产品序列号 (EBRO 不需要)
            chipSn         : '', // 芯片编号
            firmwareVersion: '', // 固件版本 (年月日)

            // 维修明细
            feedback                  : '', // 客户反馈
            beforeElectricQuantity    : '', // 维修前电量
            beforeHighTemperatureTimes: '', // 维修前高温次数
            testDetails               : '', // 检测明细

            // 维修细节
            chipReplaced              : false, // 是否更换芯片
            chipSnNew                 : '', // 新的芯片编号
            afterElectricQuantity     : '', // 维修后电量
            afterFirmwareVersion      : '', // 维修后固件版本
            afterSoftwareVersion      : '', // 维修后的软件版本
            afterPowerConsumption     : '', // 维修后的功耗

            neu: true,
            deleted: false,
        };
    }

    /**
     * 克隆工单项
     */
    static cloneWorkOrderItem(item) {
        let clone = JSON.parse(JSON.stringify(item));
        return clone;
    }
}
