<!-- 审批配置 -->
<!--
审批的 JSON 格式:
{
    type: 'ORDER',
    steps: [
        {
            step: 1,
            auditors: [
                userId: 123,
                username: 'Alice'
            ]
        },
    ]
}
-->
<template>
    <div class="audit-config">
        <div class="toolbar">
            <!-- 审批类型 -->
            <ButtonGroup class="audit-types">
                <Button v-for="type in window.AUDIT_TYPES" :key="type.value" :type="buttonType(type)" @click="switchType(type)">{{ type.name }}</Button>
            </ButtonGroup>

            <!-- 保存按钮 -->
            <Button type="primary" @click="save()">保存</Button>
        </div>


        <!-- 审批流程 -->
        <div class="box">
            <div class="title">审批流程</div>
            <div class="content audit-steps">
                <template v-for="(step, index) in auditConfig.steps">
                    <EditableLabel v-model="step.desc" :key="'desc-' + step.uid" style="margin-bottom: 10px">审批说明:</EditableLabel>
                    <!-- <div :key="'desc-' + step.uid">{{ step.desc }}</div> -->

                    <!-- 审批员 -->
                    <div :key="'step-' + step.uid" class="audit-step" :id="step.uid" :data-id="step.uid">
                        <div v-for="auditor in step.auditors" :data-id="auditor.userId" :key="auditor.userId" class="auditor">{{ auditor.nickname }}</div>
                    </div>
                    <div :key="'icon-' + step.uid" class="add-step">
                        <Icon type="ios-arrow-round-down" size="35" @click="addStep(index)"/>
                    </div>
                </template>
            </div>
        </div>

        <!-- 未分配用户 -->
        <div class='box'>
            <div class="title">未分配用户</div>
            <div class="content rest-auditors" id="rest-auditors">
                <div v-for="auditor in restAuditors" :data-id="auditor.userId" :key="auditor.userId" class="auditor">{{ auditor.nickname }}</div>
            </div>
        </div>
    </div>
</template>

<script>
import UserDao from '@/../public/static-p/js/dao/UserDao';
import AuditDao from '@/../public/static-p/js/dao/AuditDao';
import AuditUtils from '@/../public/static-p/js/utils/AuditUtils';
import Sortable from 'sortablejs';
import EditableLabel from '@/components/EditableLabel.vue';

export default {
    components: { EditableLabel },
    data() {
        return {
            auditType   : { value: '', name: '' }, // 当前的审批类型
            auditConfigs: [], // 所有审批的配置
            auditConfig : {}, // 当前业务的配置: [{ type : 'ORDER', uid: 1, users: [{ nickname: 'Bob', userId: '1' }] }]
            allAuditors : [], // 系统的所有用户
            restAuditors: [], // 当前业务未分配审批的用户
            sortables   : [], // 可拖拽对象
        };
    },
    mounted() {
        // 1. 加载所有用户和审批配置
        // 2. 给没给阶段一个 uid
        // 3. 显示第一个类型的审批配置
        Promise.all([UserDao.findUsers({ pageSize: 10000 }), AuditDao.findAuditConfigs()]).then(([users, configs]) => {
            users.forEach(user => {
                this.allAuditors.push({ userId: user.userId, username: user.username, nickname: user.nickname });
            });

            // AuditUtils.correctAuditConfigs(configs);

            // [2] 给没给阶段一个 uid
            configs.forEach(config => {
                config.steps.forEach(step => {
                    step.uid = Utils.uid();
                });
            });

            this.auditConfigs = configs;

            // [3] 显示第一个类型的审批配置
            this.switchType(window.AUDIT_TYPES[0]);
        });
    },
    methods: {
        // 切换审批类型
        switchType(type) {
            this.auditType = type;
            this.init(type.value);
        },
        // 按钮的类型: default or primary
        buttonType(type) {
            return this.auditType.value === type.value ? 'primary' : 'default';
        },
        // 初始化，参数为审批的业务类型，例如 ORDER
        init(auditType) {
            // 1. 找到当前类型的审批配置
            // 2. 如果审批配置不存在，则创建
            // 3. 找到未分配的用户
            // 4. 创建 sortable 对象

            // [1] 找到当前类型的审批配置
            this.auditConfig = this.auditConfigs.find(config => config.type === auditType);

            // [2] 如果审批配置不存在，则创建
            if (!this.auditConfig) {
                this.auditConfig = { type: this.auditType.value, steps: [this.newStep()] };
                this.auditConfigs.push(this.auditConfig);
            }

            // [3] 找到未分配的用户
            const tempIds = this.auditConfig.steps.map(step => step.auditors).flat().map(auditor => auditor.userId); // 已分配审批权限的用户 ID 数组
            this.restAuditors = this.allAuditors.filter(auditor => {
                return !tempIds.includes(auditor.userId);
            });
            this.restAuditors = Utils.clone(this.restAuditors);

            // [4] 创建 sortable 对象
            this.$nextTick(() => {
                new Sortable(document.querySelector('#rest-auditors'), { group: 'shared', animation: 150, onAdd: this.onChange, onUpdate: this.onChange });

                for (let step of this.auditConfig.steps) {
                    new Sortable(document.querySelector(`#${step.uid}`), { group: 'shared', animation: 150, onAdd: this.onChange, onUpdate: this.onChange });
                }
            });
        },
        // 添加审批阶段
        addStep(index) {
            const step = this.newStep();
            this.auditConfig.steps.splice(index+1, 0, step);

            // 创建 sortable 对象
            this.$nextTick(() => {
                new Sortable(document.querySelector(`#${step.uid}`), { group: 'shared', animation: 150, onAdd: this.onChange, onUpdate: this.onChange });
            });
        },
        // 拖拽放下用户的处理函数
        onChange(event) {
            // 1. 判断来源和目标是 auditors 还是 step
            // 2. 找到 from 和 to 的对象 (统一放到 from 和 to 的 auditors 中，方便处理)
            // 3. 删除 Sortable 创建的 DOM
            // 4. 从 from.auditors 中删除，添加到 to.auditors 中

            // [1] 判断来源和目标是 auditors 还是 step
            const fromAuditors = event.from.classList.contains('rest-auditors');
            const toAuditors   = event.to.classList.contains('rest-auditors');
            const fromStep     = event.from.classList.contains('audit-step');
            const toStep       = event.to.classList.contains('audit-step');
            const fromIndex    = parseInt(event.oldIndex);
            const toIndex      = parseInt(event.newIndex);

            let from;
            let to;

            // [2] 找到 from 和 to 的对象 (统一放到 from 和 to 的 users 中，方便处理)
            if (fromAuditors && toAuditors) {
                from = { auditors: this.restAuditors };
                to   = { auditors: this.restAuditors };
            } else if (fromAuditors && toStep) {
                from = { auditors: this.restAuditors };
                to   = this.findStep(event.to.getAttribute('data-id'));
            } else if (fromStep && toStep) {
                from = this.findStep(event.from.getAttribute('data-id'));
                to   = this.findStep(event.to.getAttribute('data-id'));
            } else if (fromStep && toAuditors) {
                from = this.findStep(event.from.getAttribute('data-id'));
                to   = { auditors: this.restAuditors };
            } else {
                log.error('onAdd 的 from 或者 to 不对');
                return;
            }

            // [3] 删除 Sortable 创建的 DOM (有的需要删除，有的不用，神奇)
            // event.item.remove();

            // [4] 从 from.auditors 中删除，添加到 to.auditors 中
            if (from === to) {
                // 同组内移动
                const temp = from.auditors[fromIndex];
                this.$set(from.auditors, fromIndex, from.auditors[toIndex]);
                this.$set(from.auditors, toIndex, temp);
            } else {
                // 不同组内移动
                const auditor = from.auditors[fromIndex];
                from.auditors.splice(fromIndex, 1);
                to.auditors.splice(toIndex, 0, auditor);
            }
        },
        // 查询 stepUid 对应的 step 对象
        findStep(stepUid) {
            return this.auditConfig.steps.find(s => s.uid === stepUid);
        },
        // 保存审批配置
        save() {
            const configs = AuditUtils.cleanAuditConfigs(this.auditConfigs);
            AuditDao.upsertAuditConfigs(configs).then(() => {
                this.$Message.success('保存成功');
            });
        },
        // 创建审批阶段对象
        newStep() {
            return {
                desc: '',
                auditors: [],
                uid: Utils.uid(),
            };
        },
    }
};
</script>

<style lang="scss">
.audit-config {
    display: grid;
    grid-template-rows: max-content 1fr;
    grid-template-columns: 1fr 250px;
    grid-gap: 20px 20px;

    .toolbar {
        display: flex;
        justify-content: space-between;
        grid-column: span 2;

        button {
            min-width: 100px;
        }
    }

    .audit-steps, .rest-auditors {
        min-height: 500px;

        .auditor {
            border: 1px solid $borderColor;
            border-radius: 4px;
            margin-bottom: 10px;
            padding: 10px;
            user-select: none;

            &:hover {
                color: $primaryColor;
                transition: color .8s;
            }
        }

        .audit-step {
            padding: 20px;
            min-height: 90px;
            border: 1px solid #5cadff55;
            border-radius: 3px;

            .auditor {
                display: inline-block;
                width: 150px;
                margin-right: 15px;
                margin-bottom: 0;
            }

            &.audit-step {
                border-style: dashed;
                border-width: 2px;
            }
        }

        .add-step {
            text-align: center;
            margin: 10px 0;

            .ivu-icon {
                color: #bbb;
            }
        }
    }
}
</style>
