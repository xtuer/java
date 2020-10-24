<!--
简介: 可编辑的 Label

参数:
title: 标题
content: 内容，可使用 v-model 进行双向绑定
inputWidth: 编辑框的宽度

Slot:
默认槽

事件:
on-change: 输入时触发，参数为输入的内容

示例:
<EditableLabel v-model="desc">审批说明:</EditableLabel>

-->
<template>
    <div class="editable-label">
        <div class="title" :class="{ 'title-margin-right': !title }"><slot>{{ title }}</slot></div>
        <Poptip :width="inputWidth" trigger="click" class="content" transfer transfer-class-name="editable-label-pop-tip">
            <span>{{ content }}</span>
            <Input v-model="contentX" slot="content" @on-change="change"/>
        </Poptip>
    </div>
</template>

<script>
export default {
    props: {
        title     : { type: String, required: false, default: '' },
        content   : { type: String, required: true },
        inputWidth: { type: Number, required: false, default: 350 }, // 输入框的宽度
    },
    model: {
        prop : 'content',
        event: 'on-change',
    },
    data() {
        return {
            contentX: this.content
        };
    },
    methods: {
        change() {
            this.$emit('on-change', this.contentX);
        }
    },
    watch: {
        content(n, o) {
            this.contentX = this.content;
        }
    }
};
</script>

<style lang="scss">
.editable-label {
    display: inline-grid;
    grid-template-columns: max-content minmax(150px, max-content);
    align-items: center;

    > .title.title-margin-right {
        margin-right: 10px;
    }

    > .content {
        cursor: default;
        border: 1px dashed #ccc;
        border-radius: 3px;

        &:hover {
            color: $primaryColor;
            border-color: $primaryColor;
            transition: all 1s;
        }

        .ivu-poptip-rel {
            display: flex;
            align-items: center;
            width: 100%;
            min-height: 1.8em;
            padding: 0 5px;
        }
    }
}

.editable-label-pop-tip .ivu-poptip-body {
    padding: 8px;
}
</style>
