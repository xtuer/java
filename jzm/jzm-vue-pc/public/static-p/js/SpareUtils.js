export default class SpareUtils {
    /**
     * 新备件
     */
    static newSpare() {
        return {
            id                  : Utils.nextId(),
            sn                  : '', // 入库单号
            type                : '', // 备件类型
            chipSn              : '', // 芯片编号
            chipProductionDate  : '', // 芯片生产时间
            chipAgingDate       : '', // 芯片老化时间
            chipPowerConsumption: '', // 芯片功耗
            chipQuantity        : 0,  // 芯片数量
            firmwareVersion     : '', // 固件版本
            softwareVersion     : '', // 软件版本
            neu                 : true,
        };
    }

    /**
     * 克隆备件
     *
     * @param {JSON} spare 备件
     * @return {JSON} 返回克隆后的备件
     */
    static cloneSpare(spare) {
        let clone = JSON.parse(JSON.stringify(spare));
        clone.chipProductionDate = spare.chipProductionDate;
        clone.chipAgingDate      = spare.chipAgingDate;

        return clone;
    }

    /**
     * 清理备件
     *
     * @param {JSON} order 订单
     * @return 无返回值
     */
    static cleanSpare(spare) {
        if (spare.neu) {
            spare.id = 0;
        }
    }
}
