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
    static cloneWorkOrder(maintenance) {
        let clone  = JSON.parse(JSON.stringify(maintenance));
        clone.date = maintenance.date; // 时间对象单独处理

        return clone;
    }
}
