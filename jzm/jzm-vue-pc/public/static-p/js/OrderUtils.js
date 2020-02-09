export default class OrderUtils {
    /**
     * 创建新订单
     *
     * @return {JSON} 返回订单对象
     */
    static newOrder() {
        return {
            id             : Utils.nextSn(),
            customerName   : '',        // 客户名称
            process        : '等待备件', // 生产进程: 等待备件、组装中
            type           : '订货',    // 订单类型: 订货、样品
            brand          : 'P+H',    // 品牌   : P+H、BD、其他
            softwareVersion: '',       // 软件版本
            personInCharge : '',         // 负责人
            orderDate      : new Date(), // 订单日期
            orderItems     : [],         // 订单项
            neu            : true,       // 新创建的订单为 true，否则为 false
        };
    }

    /**
     * 创建订单项
     *
     * @return {JSON} 返回订单项对象
     */
    static newOrderItem() {
        return {
            id        : Utils.nextSn(),
            type      : 'Pro6-T', // 产品型号
            sn        : '', // 产品序列号
            chipSn    : '', // 芯片编号
            shellColor: '', // 外壳颜色
            shellBatch: '', // 外壳批次
            sensorInfo: '', // 传感器信息
            circleInfo: '', // Ο 型圈信息
            count     : 1,  // 数量
            neu       : true,  // 新创建的订单项为 true，否则为 false
            deleted   : false, // 被删除的标记
        };
    }

    /**
     * 克隆订单
     *
     * @param {JSON} order 被克隆的订单
     * @return {JSON} 返回订单的克隆对象
     */
    static cloneOrder(order) {
        let clone  = JSON.parse(JSON.stringify(order));
        clone.orderDate = order.orderDate; // 时间对象单独处理

        return clone;
    }

    /**
     * 清理订单
     *
     * @param {JSON} order 订单
     * @return 无返回值
     */
    static cleanOrder(order) {
        // 1. 如果是新创建的 order，设置其 id 为 0
        // 2. 过滤掉新创建并且删除掉的 order item
        // 3. 如果是新创建的 order item，设置其 id 为 0

        // [1] 如果是新创建的 order，设置其 id 为 0
        if (order.neu) {
            order.id = 0;
        }

        order.orderItems = order.orderItems
            .filter(item => !(item.neu && item.deleted)) // [2] 过滤掉新创建并且删除掉的 order item
            .map(item => {
                item.id = item.neu ? 0 : item.id; // [3] 如果是新创建的 order item，设置其 id 为 0
                return item;
            });
    }
}
