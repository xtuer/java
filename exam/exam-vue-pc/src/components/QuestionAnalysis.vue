<!--
功能: 题目解析

非作答时默认答案解析可见

批改时:
    客观题: 正确答案、学员作答、题目解析
    主观题: 学员作答、参考答案、题目解析
非批改时:
    客观题: 只显示题目解析
    主观题: 参考答案、题目解析

属性:
question: 题目

案例:
<QuestionAnalysis :question="question"/>
-->
<template>
    <div class="question-analysis">
        <!-- [1] 批改时 + 客观题: 正确答案、学员作答、题目解析 -->
        <template v-if="correct && isObjectiveQuestion">
            <div>
                正确答案: <span style="width: 80px; display: inline-block">{{ correctAnswers }}</span>
                学员作答: <span :class="scoreStatusClass">{{ studentAnswers }}</span>
            </div>

            <div><a @click="analysisVisible = !analysisVisible">题目解析:</a></div>
            <transition name="slide-up">
                <div v-if="analysisVisible" v-html="question.analysis || '无'" class="color-gray"></div>
            </transition>
        </template>

        <!-- [2] 批改时 + 主观题: 参考答案、题目解析 -->
        <template v-if="correct && isSubjectiveQuestion">
            <div>学员作答: {{ studentAnswers }}</div>
            <div><a @click="analysisVisible = !analysisVisible">参考答案:</a></div>
            <transition name="slide-up">
                <div v-if="analysisVisible">
                    <div v-html="question.key || '无'" class="color-gray"></div>
                    <div>题目解析:</div>
                    <div v-html="question.analysis || '无'" class="color-gray"></div>
                </div>
            </transition>
        </template>

        <!-- [3] 非批改时 + 客观题: 只显示题目解析 -->
        <template v-if="!correct && isObjectiveQuestion">
            <div><a @click="analysisVisible = !analysisVisible">题目解析:</a></div>
            <transition name="slide-up">
                <div v-if="analysisVisible" v-html="question.analysis || '无'" class="color-gray"></div>
            </transition>
        </template>

        <!-- [4] 非批改时 + 主观题: 参考答案、题目解析 -->
        <template v-if="!correct && isSubjectiveQuestion">
            <div><a @click="analysisVisible = !analysisVisible">参考答案:</a></div>
            <transition name="slide-up">
                <div v-if="analysisVisible">
                    <div v-html="question.key || '无'" class="color-gray"></div>
                    <div>题目解析:</div>
                    <div v-html="question.analysis || '无'" class="color-gray"></div>
                </div>
            </transition>
        </template>
    </div>
</template>

<script>
import QuestionUtils from '@/../public/static-p/js/util/QuestionUtils';

export default {
    props: {
        question: { type: Object, required: true  }, // 题目
        correct : { type: Boolean, default: false }, // 是否批改
    },
    data() {
        return {
            analysisVisible: false, // 解析是否可见
        };
    },
    computed: {
        // 是否客观题
        isObjectiveQuestion() {
            return QuestionUtils.isObjectiveQuestion(this.question);
        },
        // 是主客观题
        isSubjectiveQuestion() {
            return QuestionUtils.isSubjectiveQuestion(this.question);
        },
        // 正确答案
        correctAnswers() {
            // 1. 单选题、多选题的正确答案: correct 为 true 的选项的 mark
            // 2. 判断题的正确答案: correct 为 true 的选项
            // 3. 主观题的正确答案: key
            // 4. 未作答返回 ----

            if (this.question.type === QUESTION_TYPE.SINGLE_CHOICE || this.question.type === QUESTION_TYPE.MULTIPLE_CHOICE) {
                return this.question.options.filter(q => q.correct).map(q => q.mark).join(', ');
            } else if (this.question.type === QUESTION_TYPE.TFNG) {
                if (this.question.options[0].correct) {
                    return '√';
                } else if (this.question.options[1].correct) {
                    return 'ㄨ';
                }
            } else if (this.isSubjectiveQuestion) {
                return this.question.key;
            }

            return '----';
        },
        // 学员的作答
        studentAnswers() {
            // 1. 单选题、多选题的作答: checked 为 true 的选项的 mark
            // 2. 判断题的作答: checked 为 true 的选项
            // 3. 填空题的作答: 所有 option 的 answer 合并的字符串
            // 4. 问答题的作答: 第一个 option 的 answer
            // 5. 其他情况返回 ----

            // [1] 单选题、多选题的作答: checked 为 true 的选项的 mark
            if (this.question.type === QUESTION_TYPE.SINGLE_CHOICE || this.question.type === QUESTION_TYPE.MULTIPLE_CHOICE) {
                return this.question.options.filter(q => q.checked).map(q => q.mark).join(', ') || '----';

            // [2] 判断题的作答: checked 为 true 的选项
            } else if (this.question.type === QUESTION_TYPE.TFNG) {
                if (this.question.options[0].checked) {
                    return '√';
                } else if (this.question.options[1].checked) {
                    return 'ㄨ';
                }

            // [3] 填空题的作答: 所有 option 的 answer 合并的字符串
            } else if (this.question.type === QUESTION_TYPE.FITB) {
                return this.question.options.map(option => '(' + option.answer + ')').join('、');

            // [4] 问答题的作答: 第一个 option 的 answer
            } else if (this.question.type === QUESTION_TYPE.ESSAY) {
                return this.question.options[0].answer;
            }

            // [5] 其他情况返回 ----
            return '----';
        },
        // 得分状态
        scoreStatusClass() {
            return {
                wrong         : this.question.scoreStatus === 1, // 错误
                right         : this.question.scoreStatus === 3, // 全对
                'right-half'  : this.question.scoreStatus === 2, // 半对
                'score-status': true,
            };
        },
    }
};
</script>

<style lang="scss">
.question-analysis {
    background: rgba(0, 0, 0, 0.01);

    .analysis-label {
        color: $iconColor;
    }

    .score-status {
        &.right {
            color: $successColor;
        }
        &.right-half {
            color: $warningColor;
        }
        &.wrong {
            color: $errorColor;
        }
    }

    .color-gray {
        color: #aaa;
    }
}
</style>
