<!-- 因为没电返回 BD 的表单 -->
<template>
    <div class="bd-form">
        <div class="toolbar">
            <Input v-model="productSn" placeholder="产品序列号"/>
            <Input v-model="customer" placeholder="客户名称"/>
            <Button icon="ios-search">搜索</Button>
            <span/>
            <Button @click="modal=true" type="primary">创建</Button>
        </div>

        <Modal v-model="modal" :mask-closable="false" ok-text="保存" title="因为没电返回 BD" class="bd-form" width="90%" @on-ok="save">
            <div class="form">
                <span class="label">上次出件日期</span><DatePicker v-model="editedData.deliveryDate"/>
                <span class="label">收件日期</span><DatePicker v-model="editedData.receivedDate"/>
                <span class="col-span-6"/>

                <span class="label">用户</span><Input v-model="editedData.customer"/>
                <span class="label">品牌</span><Input v-model="editedData.brand"/>
                <span class="label">型号</span><Input v-model="editedData.model"/>
                <span class="label">序列号</span><Input v-model="editedData.sn"/>
                <span class="label">芯片编号</span><Input v-model="editedData.chipCode"/>

                <span class="label">维修前电量</span><Input v-model="editedData.powerQuantityBefore"/>
                <span class="label">维修前功耗</span><Input v-model="editedData.powerDissipationBefore"/>
                <span class="label">维修前高温次数</span><Input v-model="editedData.countOfHighTemperaturBefore"/>
                <span class="label">维修前软件版本</span><Input v-model="editedData.softwareVersionBefore"/>
                <span class="label">维修前硬件版本</span><Input v-model="editedData.hardwareVersionBefore"/>

                <span class="label">维修后电量</span><Input v-model="editedData.powerQuantityAfter"/>
                <span class="label">维修后功耗</span><Input v-model="editedData.powerDissipationAfter"/>
                <span class="label">维修后高温次数</span><Input v-model="editedData.countOfHighTemperaturAfter"/>
                <span class="label">维修后软件版本</span><Input v-model="editedData.softwareVersionAfter"/>
                <span class="label">维修后硬件版本</span><Input v-model="editedData.hardwareVersionAfter"/>

                <span class="label feedback-label">客户反馈问题</span><Input v-model="editedData.feedback" class="col-span-3 feedback"/><span class="col-span-6"/>
                <span class="label check-details-label">检测问题明细</span><Input v-model="editedData.checkDetails" class="col-span-3 check-details"/><span class="col-span-6"/>
                <span class="label repair-details-label">维修明细</span><Input v-model="editedData.repairDetails" class="col-span-3 repair-details"/><span class="col-span-6"/>
            </div>
        </Modal>

        <Table :data="data" :columns="columns"/>
    </div>
</template>

<script>
export default {
    data() {
        return {
            modal: false,
            data: [],
            columns: [
                { title: '上次出件日期',   key: 'deliveryDate', width: 120, render: (h, { row: record }) => {
                    return (<span>{ dayjs(record.deliveryDate).format('YYYY-MM-DD') }</span>);
                } },
                { title: '收件日期',      key: 'receivedDate', width: 120, render: (h, { row: record }) => {
                    return (<span>{ dayjs(record.receivedDate).format('YYYY-MM-DD') }</span>);
                } },
                { title: '用户',         key: 'customer', width: 100 },
                { title: '品牌',         key: 'brand',    width: 130 },
                { title: '型号',         key: 'model',    width: 130 },
                { title: '序列号',       key: 'sn',        width: 130 },
                { title: '芯片编号',      key: 'chipCode', width: 130 },
                { title: '维修前电量',    key: 'powerQuantityBefore',    width: 130 },
                { title: '维修前功耗',    key: 'powerDissipationBefore', width: 130 },
                { title: '维修前高温次数', key: 'countOfHighTemperaturBefore', width: 130 },
                { title: '维修前软件版本', key: 'softwareVersionBefore', width: 130 },
                { title: '维修前硬件版本', key: 'hardwareVersionBefore', width: 130 },
                { title: '维修后电量',    key: 'powerQuantityAfter',    width: 130 },
                { title: '维修后功耗',    key: 'powerDissipationAfter', width: 130 },
                { title: '维修后高温次数', key: 'countOfHighTemperaturAfter', width: 130 },
                { title: '维修后软件版本', key: 'softwareVersionAfter', width: 130 },
                { title: '维修后硬件版本', key: 'hardwareVersionAfter', width: 130 },
                { title: '客户反馈问题',   key: 'feedback',      width: 300 },
                { title: '检测问题明细',   key: 'checkDetails',  width: 300 },
                { title: '维修明细',      key: 'repairDetails', width: 300 },
            ],
            editedData: {
                deliveryDate               : new Date(),
                receivedDate               : new Date(),
                customer                   : '李晓明',
                brand                      : '华为',
                model                      : '--------',
                sn                         : '--------',
                chipCode                   : '--------',
                powerQuantityBefore        : '--------',
                powerDissipationBefore     : '--------',
                countOfHighTemperaturBefore: '--------',
                softwareVersionBefore      : '--------',
                hardwareVersionBefore      : '--------',
                powerQuantityAfter         : '--------',
                powerDissipationAfter      : '--------',
                countOfHighTemperaturAfter : '--------',
                softwareVersionAfter       : '--------',
                hardwareVersionAfter       : '--------',
                feedback                   : '--------',
                checkDetails               : '--------',
                repairDetails              : '--------',
            },
            productSn: '', // 产品序列号
            customer : '', // 客户的名字
        };
    },
    mounted() {
        for (var i = 0; i < 10; i += 1) {
            this.data.push(this.editedData);
        }
    },
    methods: {
        save() {
            this.data.push(this.editedData);
        }
    }
};
</script>

<style lang="scss">
$start: 5;

.bd-form {
    .toolbar {
        display: grid;
        grid-template-columns: 250px 150px max-content 1fr 100px;
        grid-gap: 12px;
        margin-bottom: 24px;
    }

    .form {
        display: grid;
        grid-template-columns: max-content 1fr max-content 1fr max-content 1fr max-content 1fr max-content 1fr; /* 5 个输入项 */
        align-items: center;
        grid-gap: 12px 6px;

        .label {
            text-align: right;
        }
    }
}
</style>
