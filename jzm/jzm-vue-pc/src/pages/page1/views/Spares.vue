<!-- 备件管理: 备件入库出库 -->
<template>
    <div class="spare">
        <!-- 搜索工具栏 -->
        <div class="toolbar">
            <Button @click="editSpare()">创建备件</Button>
        </div>

        <!-- 备件列表 -->
        <div style="overflow: auto">
            <Table :data="spares" :columns="spareColumns" border style="min-width: 1000px">
                <template slot-scope="{ row: spare }" slot="chipQuantity">
                    <!-- 小于 20 红色 -->
                    <Badge :text="spare.chipQuantity + ''" :type="spare.chipQuantity < 20 ? 'error' : 'success'"/>
                </template>

                <template slot-scope="{ row: spare }" slot="spareAction">
                    <Poptip trigger="hover" placement="right">
                        <Icon type="md-more"/>
                        <div slot="content" class="action-buttons">
                            <Button size="small" type="error"   @click="deleteSpare(spare)">删除</Button>
                            <Button size="small" type="primary" @click="editSpare(spare)">编辑</Button>
                            <Button size="small" type="primary" @click="warehousing(spare.id, spare.chipQuantity, 1)">入库</Button>
                            <Button size="small" type="primary" @click="warehousing(spare.id, spare.chipQuantity, -1)">出库</Button>
                        </div>
                    </Poptip>
                </template>
            </Table>
        </div>

        <!-- 加载下一页按钮 -->
        <center>
            <Button v-show="more" :loading="loading" icon="md-boat" shape="circle" @click="fetchMoreSpares">更多...</Button>
        </center>

        <!-------------------------------------------------------------------------------------------------------------
                                                              编辑对话框
        -------------------------------------------------------------------------------------------------------------->
        <!-- 编辑订单对话框 -->
        <Modal v-model="modal" :mask-closable="false" title="编辑备件" width="700">
            <Form ref="spareForm" :model="editedSpare" :rules="spareRules" :key="editedSpare.id" :label-width="110" class="two-column">
                <FormItem label="入库单号:" prop="sn">
                    <Input v-model="editedSpare.sn" :readonly="!editedSpare.neu" placeholder="请输入入库单号"/>
                </FormItem>
                <FormItem label="备件类型:" prop="type">
                    <Select v-model="editedSpare.type">
                        <Option v-for="type in window.SPARE_TYPES" :value="type" :key="type">{{ type }}</Option>
                    </Select>
                </FormItem>
                <FormItem label="芯片编号:" prop="">
                    <Input v-model="editedSpare.chipSn" placeholder="请输入芯片编号"/>
                </FormItem>
                <FormItem label="芯片生产时间:" prop="">
                    <Input v-model="editedSpare.chipProductionDate" placeholder="请输入芯片生产时间"/>
                </FormItem>
                <FormItem label="芯片老化时间:" prop="">
                    <Input v-model="editedSpare.chipAgingDate" placeholder="请输入芯片老化时间"/>
                </FormItem>
                <FormItem label="芯片功耗:" prop="">
                    <Input v-model="editedSpare.chipPowerConsumption" placeholder="请输入芯片功耗"/>
                </FormItem>
                <FormItem label="芯片数量:" prop="">
                    <InputNumber v-model="editedSpare.chipQuantity" :min="0" :readonly="!editedSpare.neu" placeholder="请输入芯片数量" style="width: 100%"/>
                </FormItem>
                <FormItem label="固件版本:" prop="">
                    <Input v-model="editedSpare.firmwareVersion" placeholder="请输入固件版本"/>
                </FormItem>
                <FormItem label="软件版本:" prop="">
                    <Input v-model="editedSpare.softwareVersion" placeholder="请输入软件版本"/>
                </FormItem>
            </Form>

            <div slot="footer">
                <Button type="primary" :loading="saving" @click="saveSpare()">保存</Button>
            </div>
        </Modal>

        <!-- 出库操作对话框 -->
        <Modal v-model="warehousingData.modal" title="库存操作" width="330" class="warehousing-modal">
            <Form :label-width="60">
                <FormItem label="时间:">
                    <DatePicker v-model="warehousingData.date"
                                :editable="false"
                                format="yyyy-MM-dd"
                                placement="bottom-end"
                                placeholder="请选择日期"
                                style="width: 100%">
                    </DatePicker>
                </FormItem>
                <FormItem label="数量:" prop="softwareVersion">
                    <InputNumber v-model="warehousingData.chipQuantity" :min="1" placeholder="请输入芯片数量" style="width: 100%"/>
                </FormItem>
            </Form>
            <div slot="footer">
                <Button type="primary" :loading="saving" @click="warehousingSave()">{{ warehousingData.typeLabel }}</Button>
            </div>
        </Modal>
    </div>
</template>

<script>
import SpareUtils from '@/../public/static-p/js/SpareUtils';
import SpareDao from '@/../public/static-p/js/dao/SpareDao';

export default {
    data() {
        return {
            spares     : [],
            editedSpare: {},
            modal      : false,
            loading    : false, // 加载中
            saving     : false, // 保存中
            more       : false, // 是否还有更多用户

            filter: { // 搜索条件
                name      : '',
                pageSize  : 2,
                pageNumber: 1,
            },

            spareRules: {
                sn: [
                    { required: true, whitespace: true, message: '入库单号不能为空', trigger: 'blur' }
                ],
                type: [
                    { required: true, message: '备件类型不能为空', trigger: 'change' }
                ],
            },
            spareColumns: [
                { title: '入库单号', key: 'sn' },
                { title: '备件类型', key: 'type', width: 100 },
                { title: '芯片编号', key: 'chipSn', width: 110 },
                { title: '芯片生产时间', key: 'chipProductionDate', width: 130 },
                { title: '芯片老化时间', key: 'chipAgingDate', width: 130 },
                { title: '芯片功耗', key: 'chipPowerConsumption', width: 95 },
                { title: '芯片数量', slot: 'chipQuantity', width: 95 },
                { title: '固件版本', key: 'firmwareVersion' },
                { title: '软件版本', key: 'softwareVersion' },
                { title: '操作', slot: 'spareAction', align: 'center', width: 70 },
            ],
            // 库存操作
            warehousingData: {
                modal       : false, // 显示出库入库对话框
                spareId     : '',    // 备件 ID
                chipQuantity: 0,     // 数量
                type        : 1,     // 类型: -1 为出库，1 为入库
                typeLabel   : '',    // 类型: 出库、入库
                date        : new Date(),
            },
        };
    },
    mounted() {
        this.searchSpares();
    },
    methods: {
        // 搜索备件
        searchSpares() {
            this.spares = [];
            this.filter.pageNumber = 1;
            this.more = false;
            this.fetchMoreSpares();
        },
        // 点击更多按钮加载下一页的备件
        fetchMoreSpares() {
            this.loading = true;

            SpareDao.findSpares(this.filter).then(spares => {
                this.spares.push(...spares);

                this.filter.pageNumber++;
                this.more = spares.length >= this.filter.pageSize;
                this.loading = false;
            });
        },
        // 编辑备件: 创建或更新
        editSpare(spare) {
            // 1. 重置表单，避免上一次的验证信息影响到本次编辑
            // 2. 生成编辑对象的副本
            // 3. 显示编辑对话框

            this.$refs.spareForm.resetFields();

            if (spare) {
                // 更新
                this.editedSpare = SpareUtils.cloneSpare(spare); // 重要: 克隆对象
            } else {
                // 创建
                this.editedSpare = SpareUtils.newSpare();
            }

            this.modal = true;
        },
        // 保存备件
        saveSpare() {
            // 1. 表单验证不通过则返回
            // 2. 克隆被编辑对象
            // 3. 找到被编辑对象的下标
            // 4. 保存成功后如果是更新则替换已有对象，创建则添加到最前面
            // 5. 提示保存成功，隐藏编辑对话框

            this.$refs.spareForm.validate(valid => {
                if (!valid) { return; }

                // [2] 克隆被编辑对象
                // [3] 找到被编辑对象的下标
                this.saving = true;
                const spare = SpareUtils.cloneSpare(this.editedSpare); // 重要: 克隆被编辑的对象
                const index = this.spareIndex(spare.id);

                SpareUtils.cleanSpare(spare);
                SpareDao.saveSpare(spare).then((spareId) => {
                    // [4] 保存成功后如果是更新则替换已有对象，创建则添加到最前面
                    spare.id  = spareId;
                    spare.neu = false;

                    if (index >= 0) {
                        // 更新: 替换已有对象
                        this.spares.replace(index, spare);
                    } else {
                        // 创建: 添加到最前面
                        this.spares.insert(0, spare);
                    }

                    // [5] 提示保存成功，隐藏编辑对话框
                    this.saving = false;
                    this.modal  = false;
                    this.$Message.success('保存成功');
                });
            });
        },
        // 删除备件
        deleteSpare(spare) {
            // 1. 删除提示
            // 2. 从服务器删除成功后才从本地删除
            // 3. 提示删除成功

            this.$Modal.confirm({
                title: `确定删除 <font color="red">${spare.sn}</font> 吗?`,
                loading: true,
                onOk: () => {
                    SpareDao.deleteSpare(spare.id).then(() => {
                        const index = this.spareIndex(spare.id);
                        this.spares.remove(index); // [2] 从服务器删除成功后才从本地删除
                        this.$Modal.remove();
                        this.$Message.success('删除成功');
                    });
                }
            });
        },
        // 正编辑用户的下标
        spareIndex(spareId) {
            return this.spares.findIndex(spare => spare.id === spareId);
        },
        // 出库入库: type 为 -1 表示出库，1 表示入库
        warehousing(spareId, chipQuantity, type) {
            this.warehousingData = {
                modal       : true, // 显示出库入库对话框
                spareId,            // 备件 ID
                chipQuantity: 1,    // 数量
                type,               // 类型 : -1 为出库，1 为入库
                typeLabel   : type === -1 ? '出库': '入库',
                date        : new Date(), // 时间
            };
        },
        // 保存库存
        warehousingSave() {
            const spareId      = this.warehousingData.spareId;
            const chipQuantity = this.warehousingData.chipQuantity * this.warehousingData.type;
            const date         = this.warehousingData.date;

            SpareDao.warehousing(spareId, chipQuantity, date).then((newQuantity) => {
                const index = this.spareIndex(spareId);
                this.spares[index].chipQuantity = newQuantity;
                this.$Message.success(`${this.warehousingData.typeLabel}成功`);
                this.warehousingData.modal = false;
            });
        }
    }
};
</script>

<style lang="scss">
.spare {
    display: grid;
    grid-gap: 12px;

    .toolbar {
        display: grid;
        grid-template-columns: max-content 200px 100px;
        grid-gap: 12px;
        align-items: center;
    }

    .action-buttons button:not(:first-child) {
        margin-left: 5px;
    }
}

.warehousing-modal {
    .ivu-modal-body {
        padding-bottom: 0;
    }
}
</style>
