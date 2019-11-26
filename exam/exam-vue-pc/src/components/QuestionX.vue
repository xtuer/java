<template>
    <div :class="questionClass">
        <!-- [1] 题干 -->
        <div class="stem">
            <div>{{ question.snLabel }}</div>
            <div class="stem-content">
                <div v-html="question.stem"></div>

                <!-- 题型题:
                    A. 单选、多选、判断、填空显示每题 x 分
                -->
                <template v-if="scoreGroup">
                    ，每题 {{ question.score }} 分，共 {{ question.totalScore }} 分
                </template>
                <template v-else-if="scoreSelf">
                    （{{ question.totalScore }} 分）
                </template>
                <template v-else-if="question.type===7">
                    ，共 {{ question.totalScore }} 分
                </template>
                <template v-if="scoring">
                    <span style="color: red; margin-left: 12px">[得分: {{ question.score }} / {{ question.totalScore }}]</span>
                </template>
            </div>
        </div>

        <!-- [2] 选择题: 选项 -->
        <template v-if="question.type===1 || question.type===2">
            <div v-for="option in question.options" :key="option.id" class="option">
                <div class="mark" :class="{ correct: option.correct, checked: option.checked }" @click="answer(question, option)">
                    {{ option.mark }}
                </div>
                <div class="description" v-html="option.description"></div>
            </div>
        </template>

        <!-- [3] 判断题: 选项 (正确、错误) -->
        <template v-if="question.type===3">
            <div class="tfngs">
                <div v-for="option in question.options"
                        :key="option.id"
                        :class="{ tfng: true, correct: option.correct, checked: option.checked }"
                        @click="answer(question, option)">
                    {{ option.description }}
                </div>
            </div>
        </template>

        <!-- [4] 填空题 -->
        <template v-if="question.type===4">
            <Input v-for="option in question.options"
                   v-model="option.answer"
                   :key="option.id"
                   :readonly="!answerable"
                   placeholder="请填空..."
                   style="width: 300px"
                   @on-blur="answer(question)"/>
        </template>

        <!-- [5] 问答题 -->
        <template v-if="question.type===5">
            <Input v-model="question.options[0].answer"
                   :autosize="{minRows: 4}"
                   :readonly="!answerable"
                   type="textarea"
                   placeholder="请回答..."
                   style="width: 400px"
                   @on-blur="answer(question)"/>
        </template>

        <!-- [6] 复合题: 递归显示小题 -->
        <template v-if="question.type===6">
            <QuestionX v-for="subQuestion in question.subQuestions"
                       :key="subQuestion.id"
                       :question="subQuestion"
                       :answerable="answerable"
                       @on-answer="$emit('on-answer', subQuestion)"/>
        </template>
    </div>
</template>

<script>
export default {
    name: 'QuestionX',
    props: {
        question: { type: Object, required: true  }, // 题目
        answerable: { type: Boolean, default: false }, // 是否可作答
    },
    data() {
        return {};
    },
    methods: {
        // 提交问题的答案: 填空题和问答题时不需要 option
        answer(question, option) {
            if (!this.answerable) { return; }

            // 1. 单选题和判断题: 取消所有选项，然后选择当前选项
            if (question.type === QUESTION_TYPE.SINGLE_CHOICE || question.type === QUESTION_TYPE.TFNG) {
                question.options.forEach(o => {
                    o.checked = false;
                });
                option.checked = true;
            }
            // 2. 多选题: 置返选项
            if (question.type === QUESTION_TYPE.MULTIPLE_CHOICE) {
                option.checked = !option.checked;
            }

            this.$emit('on-answer', question);
        },
    },
    computed: {
        // 题目的样式
        questionClass() {
            return {
                'question-x'         : true,
                'question-x-group'   : this.question.type === QUESTION_TYPE.DESCRIPTION, // 题型题，题型分组
                'question-x-complex' : this.question.type === QUESTION_TYPE.COMPLEX, // 复合题
                'question-x-multiple': this.question.type === QUESTION_TYPE.MULTIPLE_CHOICE, // 多选题
                'question-x-sub'     : Utils.idIdValid(this.question.parentId), // 复合题的小题
            };
        },
        // 给小组打分的题型: 每题得分都一样
        scoreGroup() {
            // 在题型题上给分 (每题得分)，包括单选题、多选题、判断题、填空题
            return QuestionUtils.isScoreGroupQuestion(this.question);
        },
        // 给自己打分的题型: 每题得分不一样
        scoreSelf() {
            // 复合题的小题、问答题
            return QuestionUtils.isScoreSelfQuestion(this.question);
        },
        scoring() {
            return this.question.type === 1 || this.question.type === 2
                    || this.question.type === 3 || this.question.type === 4
                    || this.question.type === 5;
        }
    }
};
</script>

<style lang="scss">
.question-x {
    display: grid;
    grid-gap: 12px;
    cursor: default;

    &:not(:first-child) {
        margin-top: 12px;
    }

    &.border {
        border: 1px solid #ddd;
        border-radius: 6px;
        padding: 12px;
    }

    // 题干
    .stem {
        display: grid;
        grid-template-columns: max-content 1fr;
        align-items: start;
        position: relative;

        .stem-content {
            > div, > div > p {
                display: inline-block;
            }
        }
    }

    // 选项
    .option {
        // 标记: A, B, C, D
        display: grid;
        grid-template-columns: max-content 1fr;
        grid-gap: 8px;

        .mark {
            @include alignCenter;
            border: 1px solid rgb(211, 211, 211);
            border-radius: 100%;
            transition: all .6s;
            width : 28px;
            height: 28px;
            cursor: pointer;

            &.correct {
                // color: white;
                // background: $lightPrimaryColor;
                // border-color: green;
                box-shadow: 0 0 10px green inset;
            }
            &.checked {
                color: white;
                background: $lightPrimaryColor;
            }
        }

        .description {
            padding-top: 3px;
        }
    }

    // 判断题
    .tfngs {
        display: grid;
        grid-template-columns: 1fr 1fr;
        width: 130px;
        border-radius: 4px;
        border: 1px solid #eee;
        overflow: hidden;
        cursor: pointer;

        .tfng {
            @include alignCenter;
            font-size: 12px;
            height: 24px;

            &.correct {
                // color: white;
                // background: $lightPrimaryColor;
                // border: 1px solid green;
                // border-color: green;
                box-shadow: 0 0 6px green inset;
            }
            &.checked {
                color: white;
                background: $lightPrimaryColor;
            }
        }
    }

    &.question-x-group .stem {
        font-size: 18px;
        font-weight: bold;
    }

    &.question-x-multiple .mark {
        border-radius: 4px;
    }
}
</style>
