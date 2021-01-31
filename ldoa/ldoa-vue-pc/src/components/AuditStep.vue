<!-- eslint-disable vue/no-parsing-error -->
<!--
功能: 审批阶段

参数:
step: 审批阶段

示例:
<AuditStep :step="step"/>
-->
<template>
    <div class="audit-step" :style="auditStyle()">
        <!-- [1] 需要当前用户审批 -->
        <div v-if="needAudit" class="audit-toolbar">
            <!-- 审批的信息 -->
            <Input v-model="comment" :rows="6" type="textarea"/>

            <!-- 审批的工具栏 -->
            <div class="audit-toolbar-top">
                <FileUpload v-if="step.needAttachment" @on-success="fileUploaded">{{ attachment.filename || '上传文件' }}</FileUpload>
                <span class="stretch"></span>

                <!-- 下一阶段的审批员 (非最后一个阶段才显示) -->
                <Select v-if="!step.lastStep" v-model="nextStepAuditorId" class="prepend-label" data-prepend-label="流转到" style="width: 200px">
                    <Option v-for="auditor in step.nextStepAuditors" :key="auditor.userId" :value="auditor.userId">{{ auditor.nickname }}</Option>
                </Select>

                <Button type="error" @click="reject">拒绝</Button>
                <Button type="primary" @click="accept">通过</Button>
            </div>
        </div>

        <!-- [2] 不需要当前用户审批，显示为只读 -->
        <template v-else>
            <!-- 审批信息 -->
            <pre class="text-color-gray">{{ step.comment || '无' }}</pre>

            <div class="comment-bottom">
                <!-- 附件 -->
                <div v-if="step.attachment">
                    <span class="text-color-gray margin-right-5">附件:</span>
                    <a :href="step.attachment.url">{{ step.attachment.filename }}</a>
                </div>
                <div v-else></div>

                <!-- 审批员和审批时间 -->
                <div v-if="step.state >= 1" class="sign">
                    <!-- 等待审批或者已经审批，显示执行审批的审批员名字 -->
                    {{ step.auditorNickname }} / {{ step.processedAt | formatDate }}
                </div>
                <div v-else class="sign">
                    <!-- 还没有审批，显示所有审批员的名字 -->
                    {{ step.auditors.map(a => a.nickname).join(', ') }} / ---
                </div>
            </div>

            <!-- 状态的图标 -->
            <Icon v-if="stateIcon" :type="stateIcon.icon" :color="stateIcon.color" class="state-icon"/>
        </template>
    </div>
</template>

<script>
import AuditDao from '@/../public/static-p/js/dao/AuditDao';
import FileUpload from '@/components/FileUpload.vue';

export default {
    props: {
        step: { type: Object, required: true }, // 审批项
        auditable: { type: Boolean, default: false }, // 是否能进行审批
    },
    components: { FileUpload },
    computed: {
        // 当前登陆用户
        me() {
            return this.$store.state.user;
        },
        // 需要审批返回 true，否则返回 false
        needAudit() {
            // 待审批且审批人是当前登陆用户
            if (this.step.state === 1 && this.isCurrentUser(this.step.auditorId)) {
                return true;
            } else {
                return false;
            }
        },
        // 审批状态的图标
        stateIcon() {
            const icons = ['md-bicycle', 'md-bicycle', 'md-close', 'md-checkmark'];
            const colors = ['#e8eaec', '#808695', '#ed4014', '#19be6b'];

            if (this.step.state === 0 || this.step.state === 1) {
                return {
                    icon: icons[this.step.state],
                    color: colors[this.step.state],
                };
            } else {
                return null;
            }
        }
    },
    data() {
        return {
            comment: '', // 审批意见
            attachment: { attachmentId: '0', filename: '' }, // 附件
            nextStepAuditorId: '0', // 下一阶段审批员 ID
        };
    },
    mounted() {
        // 需要审批时，默认显示审批的模板
        if (this.needAudit) {
            this.comment = this.step.comment || this.step.commentTemplate;
        }
    },
    methods: {
        // 通过审批
        accept() {
            this.audit(true);
        },
        // 拒绝审批
        reject() {
            this.audit(false);
        },
        // 审批
        audit(accepted) {
            if (accepted && !this.validate()) {
                return;
            }

            const action = accepted ? '通过' : '拒绝';
            this.$Modal.confirm({
                title: `确定 <font color="red">${action}</font> 吗?`,
                loading: true,
                onOk: () => {
                    this.step.comment = this.comment.trim();

                    AuditDao.acceptAuditStep(
                        this.step.auditId,
                        this.step.step,
                        accepted,
                        this.comment,
                        this.attachment.attachmentId,
                        this.nextStepAuditorId
                    ).then(() => {
                        this.step.state = accepted ? 3 : 2;
                        this.$Message.success('审批完成');
                        this.$Modal.remove();
                    });
                }
            });
        },
        // 校验数据是否有效，有效返回 true，否则返回 false
        validate() {
            // 附件检测
            if (this.step.needAttachment && !Utils.isValidId(this.attachment.attachmentId)) {
                this.$Message.error('请上传附件');
                return false;
            }

            // 非最后一阶段需要选择下一阶段的审批员
            if (!this.step.lastStep && !Utils.isValidId(this.nextStepAuditorId)) {
                this.$Message.error('请选择流转到的审批员');
                return false;
            }

            return true;
        },
        auditStyle() {
            if (this.step.state === 2) {
                // 审批不通过
                return {
                    backgroundImage: 'url(/static-p/img/unpass.png)'
                };
            } else if (this.step.state === 3) {
                // 审批通过
                return {
                    backgroundImage: 'url(/static-p/img/pass.png)'
                };
            } else {
                // 其他
                return { };
            }
        },
        // 附件上传成功
        fileUploaded(file) {
            this.attachment.filename = file.filename;
            this.attachment.attachmentId = file.id;
        }
    }
};
</script>

<style lang="scss">
.audit-step {
    position: relative;
    background-repeat: no-repeat no-repeat;
    background-size: 100px auto;
    background-position: top right;

    .comment-bottom {
        display: flex;
        justify-content: space-between;
        margin-top: 40px;
    }

    .toolbar {
        display: flex;
        margin-top: 10px;

        button {
            margin-left: 10px;
        }
    }

    .state-icon {
        position: absolute;
        top: 1px;
        right: 15px;
        font-size: 40px;
    }

    .audit-toolbar {
        flex-direction: column;

        .audit-toolbar-top {
            display: flex;
            margin-top: 2px;
        }

        textarea, .prepend-label::before, .ivu-select-selection, .audit-toolbar-top button {
            border-radius: 0;
        }

        textarea {
            border-color: $lightPrimaryColor;
        }

        .file-upload {
            max-width: 300px;
            overflow: hidden;

            .ivu-btn-primary {
                color: $iconColor;
                border-color: $borderColor;
                background: white;

                :hover {
                    color: $primaryColor;
                    transition: all .6s;
                }
            }
        }
    }
}
</style>
