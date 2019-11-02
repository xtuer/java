<!--
功能: 题目组件

参数:
question: 题目
editable: 是否可编辑

事件: 无

槽: 无

示例:
<Question :question="question" editable/>

样式:
question | question-sub
    stem
    option
    key
    answer
-->
<template>
    <div :class="questionClass">
        <!-- 可编辑的题目 -->
        <template v-if="editable">
            <!--
                1. 题型题: 题干
                2. 复合题: 题干，递归显示小题
                3. 判断题: 题干、选项、答案、解析
                4. 单选题、多选题: 题干、选项、答案、解析
                5. 填空题、问答题: 题干、答案、解析
             -->

            <!-- [1] 题型题: 题干 -->
            <!-- [2] 复合题: 题干，递归显示小题 -->
            <!-- [3] 判断题: 题干、选项、答案、解析 -->
            <!-- [4] 单选题、多选题: 题干、选项、答案、解析 -->
            <!-- [5] 填空题、问答题: 题干、答案、解析 -->
        </template>

        <!-- 只读题目 -->
        <template v-else>

        </template>
    </div>
</template>

<script>
export default {
    name: 'Question', // 递归组件需要设置 name 属性，才能在模板中调用自己
    props: {
        question: { type: Object, required: true },
        editable: { type: Boolean, default: false },
    },
    data() {
        return {};
    },
    mounted() {},
    methods: {},
    computed: {
        questionClass() {
            return {
                question          : true,
                'question-group'  : true, // 题型题，题型分组
                'question-complex': true, // 复合题
                'question-sub'    : true, // 复合题的小题
            };
        }
    }
};
</script>

<style lang="scss">

</style>
