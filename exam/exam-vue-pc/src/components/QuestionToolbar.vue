<!--
功能: 题目的工具栏

属性:
question: 题目

事件:
on-append-question-to-group-click(question): 点击添加题目到题型中的按钮，参数 question
on-edit-question-click(question)  : 点击编辑题目按钮，参数 question
on-delete-question-click(question): 点击删除题目按钮，参数 question
on-move-down-click(question)      : 点击向下移动题目按钮，参数 question
on-move-up-click(question)        : 点击向上移动题目按钮，参数 question
-->

<template>
    <div class="question-toolbar">
        <Icon v-if="question.type===7" type="md-add-circle" @click="$emit('on-append-question-to-group-click', question)"/>
        <Icon v-if="question.type!==7" type="ios-create" @click="$emit('on-edit-question-click', question)"/>
        <Icon type="md-trash" @click="$emit('on-delete-question-click', question)"/>
        <Icon v-if="question.type!==7" type="md-arrow-round-down" @click="$emit('on-move-down-click', question)"/>
        <Icon v-if="question.type!==7" type="md-arrow-round-up" @click="$emit('on-move-up-click', question)"/>
    </div>
</template>

<script>
export default {
    props: {
        question: { type: Object, required: true }, // 题目
    },
};
</script>

<style lang="scss">
.question-toolbar {
    display    : flex;
    position   : absolute;
    right      : -1px;
    top        : -28px;
    height     : 28px;
    line-height: 28px;
    background : $lightPrimaryColor;
    opacity    : 0;
    padding    : 0 8px;
    align-items: center;
    border-radius: 3px 3px 0 0;

    .ivu-icon {
        font-size: 18px;
        cursor: pointer;
        margin-left: 6px;

        &:first-child {
            margin-left: 3px;
        }

        &:hover {
            color: white;
            transition: all .6s;
        }
    }
}
</style>
