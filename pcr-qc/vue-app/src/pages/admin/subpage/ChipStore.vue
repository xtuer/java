<template>
    <div class="chip-store">
        <Modal v-model="modal" :mask-closable="false" ok-text="保存" title="芯片入库" class="chip-form-modal" @on-ok="saveChip">
            <div class="chip-form">
                <span>芯片编号</span><Input v-model="editingChip.code"/>
                <span>备件类型</span>
                <Select v-model="editingChip.type">
                    <Option v-for="type in chipTypes" :value="type" :key="type">{{ type }}</Option>
                </Select>
                <span>芯片功耗</span><InputNumber v-model="editingChip.consumption" style="width: 100%"/>
                <span>生产时间</span><DatePicker v-model="editingChip.productionDate"/>
                <span>老化时间</span><DatePicker v-model="editingChip.agingDate"/>
                <span>入库日期</span><DatePicker v-model="editingChip.warehousingDate"/>
            </div>
        </Modal>

        <div class="toolbar">
            <Input v-model="filter.chipCode" placeholder="芯片编码"/>
            <DatePicker v-model="filter.productionDateRange" type="daterange" placeholder="生产时间范围" separator=" 至 " split-panels confirm/>
            <Button icon="ios-search" @click="searchChips">搜索</Button>
            <span/>
            <Button @click="editChip(-1)" type="primary">创建</Button>
        </div>

        <!-- 芯片列表 -->
        <Table :data="chips" :columns="columns">
            <template slot-scope="{ row: chip }" slot="productionDateSlot">
                {{ chip.productionDate | formatDate }}
            </template>
            <template slot-scope="{ row: chip }" slot="agingDateSlot">
                {{ chip.agingDate | formatDate }}
            </template>
            <template slot-scope="{ row: chip }" slot="warehousingDateSlot">
                {{ chip.warehousingDate | formatDate }}
            </template>
            <template slot-scope="{ index }" slot="action">
                <Poptip trigger="hover" title="确定删除芯片?" confirm transfer @on-ok="deleteChip(index)">
                    <Icon type="ios-close-circle" size="16"/>
                </Poptip>
                <Icon type="ios-create" size="16" @click="editChip(index)"/>
            </template>
        </Table>
    </div>
</template>

<script>
/**
 * 创建一个芯片对象
 */
function newChip() {
    return {
        code           : '',         // 芯片编号
        type           : '',         // 备件类型
        consumption    : 0,          // 芯片功耗
        productionDate : new Date(), // 生产时间
        agingDate      : new Date(), // 老化时间
        warehousingDate: new Date(), // 入库日期
    };
}
export default {
    props: {},
    data() {
        return {
            modal: false,
            editingIndex: -1,        // 被编辑的芯片的下标，-1 表示创建，大于等于 0 表示编辑已有的
            editingChip : newChip(), // 被编辑的芯片

            // 搜索条件
            filter: {
                chipCode: '',
                productionDateRange: [],
            },

            // 芯片数组
            chips  : [],
            columns: [
                { type: 'index', width: 60, align: 'center' }, // 显示行号
                { title: '芯片编号', key: 'code' },
                { title: '备件类型', key: 'type' },
                { title: '芯片功耗', key: 'consumption' },
                { title: '生产时间', slot: 'productionDateSlot' },
                { title: '老化时间', slot: 'agingDateSlot' },
                { title: '入库日期', slot: 'warehousingDateSlot' },
                { title: '操作', slot: 'action', width: '100px', align: 'center' },
            ],
        };
    },
    mounted() {
        this.chips.push(newChip());
    },
    methods: {
        editChip(index) {
            // 1. 保存编辑的芯片下标
            // 2. 更新: index 大于等于 0 表示编辑已有的芯片
            // 3. 创建: index 小于 0 表示创建一个新的芯片对象

            // [1] 保存编辑的芯片下标
            this.editingIndex = index;

            if (this.editing) {
                // [2] 更新: index 大于等于 0 表示编辑已有的芯片
                // 编辑当前行芯片的备份，保存时才进行替换
                this.editingChip = Object.assign({}, this.chips[this.editingIndex]);
            } else {
                // [3] 创建: index 小于 0 表示创建一个新的芯片对象
                this.editingChip = newChip();
            }

            this.modal = true; // 显示弹窗
        },
        saveChip() {
            if (this.editing) {
                this.$set(this.chips, this.editingIndex, this.editingChip);
            } else {
                this.chips.push(this.editingChip);
            }
        },
        deleteChip(index) {
            this.chips.splice(index, 1);
        },
        searchChips() {
            console.log(Utils.dateTimeRange(this.filter.productionDateRange[0], this.filter.productionDateRange[1]));
        }
    },
    computed: {
        editing() {
            return this.editingIndex >= 0;
        },
        chipTypes() {
            return CHIP_TYPES;
        }
    },
    filters: {
        // 格式化字符串过滤器
        formatDate(date) {
            return dayjs(date).format('YYYY-MM-DD');
        }
    }
};
</script>

<style lang="scss">
.chip-store {
    .toolbar {
        display: grid;
        grid-template-columns: 150px 200px max-content 1fr 100px;
        grid-gap: $gap;
        margin-bottom: 2*$gap;
    }

    .ivu-icon {
        &:first-child {
            margin-right: $gap;
        }
        &:hover {
            color: $primaryColor;
            cursor: pointer;
        }
    }
}

.chip-form-modal .chip-form {
    display: grid;
    grid-template-columns: max-content 1fr max-content 1fr;
    grid-gap: $gap;
    align-items: center;
}
</style>
