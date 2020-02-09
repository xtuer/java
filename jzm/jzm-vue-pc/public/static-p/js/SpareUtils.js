export default class SpareUtils {
    /**
     * 新备件
     */
    static newSpare() {
        return {
            id                  : Utils.nextId(),
            sn                  : '', // 入库单号
            type                : '', // 备件类型
            shipSn              : '', // 芯片编号
            shipProductionDate  : '', // 芯片生产时间
            shipAgingDate       : '', // 芯片老化时间
            shipPowerConsumption: '', // 芯片功耗
            shipQuantity        : 0,  // 芯片数量
            firmwareVersion     : '', // 固件版本
            softwareVersion     : '', // 软件版本
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
        clone.shipProductionDate = spare.shipProductionDate;
        clone.shipAgingDate      = spare.shipAgingDate;

        return clone;
    }
}
